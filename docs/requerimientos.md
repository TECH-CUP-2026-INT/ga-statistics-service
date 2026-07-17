# Requirements

## Functional

- **RF1**: Register a completed match event with per-player statistics.
- **RF2**: Query a player's average win rate, optionally filtered by tournament.
- **RF3**: Query a player's average goals, fouls, and minutes played.
- **RF4**: Query the total number of matches played by a player.
- **RF5**: Query a player's total goals, fouls, and assists.
- **RF6**: Query a player's yellow and red cards.
- **RF7**: Query a team's overall statistics in a tournament.
- **RF8**: Query a team's match record (wins, draws, losses).
- **RF9**: Query a team's average and total goals and fouls.
- **RF10**: Query a team's goals for, against, and goal difference.
- **RF11**: Query the tournament standings table.
- **RF12**: Query public player rankings (goals, wins, fair play, minutes).
- **RF13**: Query goalkeeper rankings (fewest goals conceded).
- **RF14**: Generate and retrieve tournament recognitions (top scorer, best defense).
- **RF15**: Query per-match total cards and result.

## Non-functional

- **RNF1**: The service must be stateless and horizontally scalable.
- **RNF2**: All queries must respond in under 2 seconds for the expected tournament data volume.
- **RNF3**: The service must not depend on other services being available for read operations (except tournament standings and active tournament resolution).
- **RNF4**: Test coverage must be at least 80% (currently 97%).
