# Traffic Light Intersection Analysis(2024)

## Overview
This project analyzes traffic intersections to evaluate their efficiency. It simulates and measures key performance metrics such as:

- Dead time at traffic lights (periods when no vehicles are crossing)
- Wait times for vehicles at traffic lights
- Overall intersection efficiency

## Project Structure
- `ProyectoSemaforoT.clj`: Main project file containing all traffic analysis logic
- Configuration files: Multiple `config{n}.txt` files containing intersection parameters
- Data files: Corresponding `data{n}.txt` files with vehicle arrival data

## Key Features
- Analyzes multiple intersections in parallel using Clojure's `pmap`
- Calculates average waiting times per intersection
- Identifies intersections with highest/lowest waiting times
- Detects traffic lights with excessive "dead time"
- Generates comprehensive statistics and reports

## Usage
To run the analysis:

```clojure
(-mainFinal)
```

The program will:

1. Ask for intersection IDs to analyze
2. Process each intersection in parallel
3. Display results as they become available
4. Allow you to view detailed statistics for specific intersections
5. Generate a comprehensive report in `outFinal.txt`

## Key Functions
- `tiempo-pasar-individual`: Calculates individual vehicle waiting time
- `tiempo-muerto-crucero`: Calculates dead time at an intersection
- `promedio-tiempos`: Calculates average wait times

## Output Statistics
The analysis provides:

- Number of vehicles passing through each intersection
- Average waiting time across all intersections
- Top 10% of intersections with highest waiting times
- Top 10% of intersections with lowest waiting times
- Traffic lights with highest dead time periods

## Requirements
- Clojure environment
- Configuration and data files properly formatted

## Example Configuration
See the multiple config files for examples of intersection specifications.