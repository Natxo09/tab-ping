# Plan de actualización — Tab Ping

## Estado actual (v0.3.0-B-1.21.1)

El mod está en MC 1.21.1 con Yarn mappings, Java 17, y versiones muy antiguas de Fabric.
Tiene bugs conocidos y crashes en servidores. Se descarta fixear la versión actual.

## Fase 1: MC 1.21.10 + Mojang mappings + bug fixes

### Actualización de dependencias

| Componente | Actual | Nuevo |
|---|---|---|
| Minecraft | 1.21.1 | 1.21.10 |
| Mappings | Yarn | Mojang official |
| Fabric Loader | 0.15.11 | 0.18.4 |
| Fabric Loom | 1.6-SNAPSHOT | 1.15-SNAPSHOT (remap) |
| Fabric API | 0.102.1+1.21.1 | 0.138.4+1.21.10 |
| Java | 17 | 21 |
| Gradle | 8.7 | 9.3.0 |

### Migración Yarn → Mojang (clases conocidas)

| Yarn (actual) | Mojang (nuevo) |
|---|---|
| `MinecraftClient` | `Minecraft` |
| `PlayerListHud` | `PlayerTabOverlay` |
| `PlayerListEntry` | `PlayerInfo` |
| `TextRenderer` | `Font` |
| `DrawContext` | `GuiGraphics` |
| `Text` | `Component` |
| `ClientCommandManager` | Fabric API (sin cambio) |

**IMPORTANTE:** Los nombres exactos deben verificarse con `javap` ya que pueden
variar entre versiones. Los de arriba son orientativos.

### Reescritura del Mixin (PlayerListHudMixin)

El mixin actual inyecta en dos puntos de `PlayerListHud`:

1. `@ModifyArg` en `render()` → `Math.min(II)I` ordinal 0
2. `@Inject` en `renderLatencyIcon()` con cancellable

**Pasos necesarios:**

1. Encontrar la clase equivalente en Mojang mappings (probablemente `PlayerTabOverlay`)
2. Verificar con `javap` que `renderLatencyIcon` existe y su firma
3. Verificar que `render()` sigue usando `Math.min` y en qué posición
4. Actualizar el access widener con el nuevo nombre de clase
5. Reescribir el mixin con los nuevos nombres

### Bug fixes

1. **Thread safety en DevPingManager**
   - Campos `simulatedPing` y `enabled` son `static` sin `volatile`
   - Se acceden desde el thread de comandos y el thread de render
   - Fix: añadir `volatile` a ambos campos

2. **Código redundante en PlayerListHudMixin**
   - Condiciones duplicadas en color (>=600 y >=1000 son iguales)
   - Condiciones duplicadas en formato de texto (>999 y else son iguales)
   - Fix: simplificar a `ping > 9999 ? "9999+" : String.valueOf(ping)`

3. **Limpieza general**
   - Eliminar `PingTabModDataGenerator.java` (placeholder vacío)
   - Eliminar entrypoint `fabric-datagen` del fabric.mod.json
   - Eliminar `pingtabmod.mixins.json` (server-side mixins vacío, no se necesita)
   - Actualizar `pingtabmod.client.mixins.json` compatibilityLevel a JAVA_21
   - Eliminar `suggests: "another-mod"` del fabric.mod.json (placeholder)
   - Cambiar `environment` a `"client"` en fabric.mod.json (es client-only)
   - Actualizar el issues link en fabric.mod.json al repo correcto

### Infraestructura

- Crear `CLAUDE.md` (ya hecho)
- Crear `.github/workflows/publish.yml` (Modrinth + GitHub Release)
- Actualizar `.github/workflows/build.yml` (Java 25)
- Añadir plugin `com.modrinth.minotaur` al build.gradle
- Crear `.env` con MODRINTH_TOKEN (gitignored)
- Añadir `.env` al `.gitignore`

## Fase 2: MC 1.21.11

### Cambios esperados (basado en experiencia con quick-homes)

- `ResourceLocation` → `Identifier`
- `ResourceKey.location()` → `ResourceKey.identifier()`
- Posibles cambios menores en APIs de rendering

### Versión

- Bump de versión (ej. 1.1.0)
- Tag + publish

## Branching

```
master (v0.3.0-B-1.21.1, roto)
  └── develop
       ├── feature/mc-1.21.10-mojang (Fase 1)
       └── feature/mc-1.21.11 (Fase 2)
```

## Notas

- El mod es client-only, no necesita entrypoint de servidor realmente
- El `PingTabMod.java` (ModInitializer) podría eliminarse si no se necesita
  lógica server-side, pero se mantiene por si se añade en el futuro
- El access widener debe actualizarse con el nombre Mojang de la clase
