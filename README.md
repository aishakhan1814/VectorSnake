# VectorSnake — Java 2D Game

A small desktop Snake game built with Java Swing. It demonstrates grid-based movement,
2D direction vectors, collision detection, a game loop, keyboard input, and score tracking.

## Requirements
- JDK 8 or later
- No external dependencies

## Run
From this folder:

```bash
javac -d out src/VectorSnake.java
java -cp out VectorSnake
```

## Controls
- Arrow keys or WASD: move
- Space: restart after game over

## Technical notes
- Movement uses a 2D direction vector `(dx, dy)` constrained to the grid.
- Collision checks cover wall boundaries and the snake's body.
- A Swing timer advances the game at a 100 ms tick interval (nominally 10 updates/sec).
- The nominal update interval is a game setting, **not a measured frame-rate benchmark**.
- Food spawning avoids occupied snake cells.

## Possible extensions
- Add difficulty levels and a pause menu.
- Track average update duration and paint duration with `System.nanoTime()`.
- Add unit tests for collision and direction-change logic.
- Add a high-score file and a replay mode.

## License
MIT
