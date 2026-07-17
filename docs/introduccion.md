# Introduction

## Context

TechCup Fútbol is the platform that digitalizes the semester football tournament of the Systems Engineering, Artificial Intelligence, Cybersecurity, and Statistical Engineering programs at the Escuela Colombiana de Ingeniería Julio Garavito. It is built as a set of microservices, each responsible for a business domain (identity, users, teams, tournaments, competition, logistics, communications, statistics, etc.).

## This microservice

`ga-statistics-service` owns the **statistics** domain. It does not compute anything in real-time during a match — it receives resolved events from the Competition service (live refereeing) once a match finishes, and from there computes everything else: averages, totals, rankings, and recognitions.

## Problem it solves

Before this service, tournament statistical information (top scorers, standings, fair play) did not exist in a centralized or real-time manner — it depended on manual calculations or scattered match reports. This service:

- Centralizes all statistics in a single place, queryable by any other service or the frontend.
- Computes everything **from raw data** (one record per player per match), without relying on another service to send pre-computed averages.
- Is **read-only** for the rest of the system except for two exceptions: the ingestion event (`POST /events`, called only by Competition) and the recognition trigger (`POST /tournaments/{id}/recognitions`, called only by Tournaments).

## Scope

Covers statistics for:

- **Player**: averages and totals for goals, fouls, minutes, assists, cards, matches played, win rate.
- **Team**: match record (W/D/L), goals for/against, average goals and fouls per match, tournament standings.
- **Tournament**: overall per-match averages, total cards, public rankings (top scorers, fair play, best defense), official recognition upon completion.
- **Match**: result per team (including walkovers), total cards.

Does not cover: match scheduling, lineups, or live refereeing logic — those belong to the Competition service.
