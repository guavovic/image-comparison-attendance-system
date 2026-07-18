# Attendance System with Image Comparison

Java desktop application that registers employee attendance by comparing a captured photo against a set of stored reference images.

## About

Academic project built to explore image processing fundamentals in pure Java, without external computer vision libraries. The system loads an employee photo and decides whether it matches a registered person by combining three classic similarity techniques.

> **Note:** this project does not use machine learning or face detection. It compares whole images statistically, which was the scope of the assignment.

## How It Works

1. A reference image (`funcionario.png`) is loaded from the validation folder.
2. Every image in the `database` folder is resized to 100x100 and compared against it.
3. Three similarity scores are computed and combined by weight:

| Technique | Weight | Implementation |
|---|---|---|
| Histogram similarity | 0.4 | 256-bin histogram over the red channel |
| Pixel-by-pixel comparison | 0.3 | Direct per-pixel difference |
| Distance metric | 0.3 | Euclidean and Manhattan distance over histograms |

4. If the weighted score reaches **0.85**, the match is accepted and the attendance record is written with a timestamp.

## Architecture

The application starts from `visuais/TelaCentral.java`.

## Tech Stack

Java (Swing, AWT, ImageIO). No external dependencies.

## How to Open

Eclipse project. Import the `sistema-bater-ponto-ia-projeto` folder as an existing Java project and run `TelaCentral`.

## Known Limitations

Documented honestly, as this is an archived academic project:

- Compares whole images rather than faces, so lighting or clothing changes affect the result.
- The histogram uses only the red channel, ignoring green and blue.
- File paths are hardcoded for Windows and resolved through machine detection.
- Attendance records are written to a fixed folder instead of the matched user's.

## Status

Academic project (2022) — archived and no longer maintained.
