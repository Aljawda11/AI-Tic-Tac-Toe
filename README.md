# AI Tic-Tac-Toe

A Java-based Tic-Tac-Toe game with a graphical user interface, multiple game modes, configurable settings, and AI opponents with different difficulty levels.

The project demonstrates Java programming, object-oriented design, GUI development, database integration, and game-playing algorithms such as Minimax with Alpha-Beta pruning.

## Features

- Single-player and multiplayer game modes
- Three AI difficulty levels:
  - **Easy** — Random move selection
  - **Medium** — Attempts winning moves, blocks the opponent, then selects an available move
  - **Hard** — Uses the Minimax algorithm with Alpha-Beta pruning
- Configurable board and game settings
- Custom player names and symbols
- Game timer and player information
- Game history storage
- Persistent settings using MySQL
- Java Swing graphical user interface
- Audio support

## Technologies Used

- Java
- Java Swing
- MySQL
- JDBC
- Object-Oriented Programming (OOP)
- Minimax Algorithm
- Alpha-Beta Pruning

## Project Structure

| File | Purpose |
| --- | --- |
| `Main.java` | Application entry point |
| `WelcomeFrame.java` | Main welcome interface |
| `GameFrame.java` | Main game interface |
| `GameLogic.java` | Core Tic-Tac-Toe game logic |
| `BotAI.java` | AI difficulty logic and Minimax implementation |
| `GameSettings.java` | Stores game configuration |
| `SettingsFrame.java` | Settings interface |
| `NameInputDialog.java` | Player name input |
| `DatabaseManager.java` | MySQL database connection and persistence |
| `AudioManager.java` | Handles game audio |

## Database Setup

The application uses a MySQL database named:

```sql
tictactoe
```

Create the database before running the application:

```sql
CREATE DATABASE tictactoe;
```

The application automatically creates the required `settings` and `game_history` tables when it starts.

Database configuration can be provided using the following environment variables:

```text
TICTACTOE_DB_URL
TICTACTOE_DB_USER
TICTACTOE_DB_PASS
```

Default local configuration:

```text
URL: jdbc:mysql://localhost:3306/tictactoe
User: root
Password: empty
```

A MySQL JDBC Connector is required to run the database functionality.

## How to Run

1. Install Java and MySQL.
2. Create the `tictactoe` database.
3. Add MySQL Connector/J to the project classpath.
4. Compile the Java source files.
5. Run `Main.java`.

## AI Implementation

The game includes different AI strategies based on the selected difficulty.

The **Hard** difficulty uses the **Minimax algorithm with Alpha-Beta pruning** to evaluate possible moves and improve decision-making efficiency.

## Author

**Abdullah Ibrahim Aljawda**  
Software Engineering Student — Universiti Tun Hussein Onn Malaysia (UTHM)
