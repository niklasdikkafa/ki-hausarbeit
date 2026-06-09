# Effizienzsteigerung durch Genetische Algorithmen

Analyse und Implementierung naturanaloger Optimierungsverfahren am Beispiel des Traveling Salesman Problems (TSP). 

Diese Implementierung entstand als Begleitprojekt zur Hausarbeit im Modul "Künstliche Intelligenz" (B.Sc. Angewandte Informatik) an der Hochschule für Angewandte Wissenschaften Hamburg (HAW Hamburg) im Juli 2024.

---

## Projektbeschreibung

Das Projekt befasst sich mit der praktischen Lösung des kombinatorischen Optimierungsproblems "Traveling Salesman Problem" (TSP). Da exakte Lösungsansätze bei großen Knotenmengen zu komplex sind, werden in diesem Repository Metaheuristiken eingesetzt und miteinander verglichen. 

Der Fokus liegt auf der Funktionsweise von Genetischen Algorithmen (GAs) sowie deren Operatoren. Als Vergleichsbasis dienen ein Ameisenalgorithmus (Ant Colony Optimization) und eine klassische Einfügeheuristik (Nearest Insertion).

---

## Implementierte Komponenten

Das Softwareprojekt ist in Java umgesetzt und teilt sich in folgende algorithmische Ansätze auf:

### 1. Genetischer Algorithmus (GA)
* **Selektion:** Turniersubauswahl (Tournament Selection), Roulette-Rad-Auswahl (Roulette Wheel Selection) und Elitismus.
* **Crossover (Rekombination):** Order Crossover (OX), Cycle Crossover (CX) und Partially Mapped Crossover (PMX).
* **Mutation:** Inversion Mutation, Swap Mutation und Cyclic Shift Mutation.

### 2. Ameisenalgorithmus (ACO)
* Stochastische Wegfindung künstlicher Ameisen basierend auf einer dynamischen Pheromon-Matrix und der inversen Distanz als Heuristik.

### 3. Heuristik
* Nearest-Insertion-Verfahren zur Generierung einer schnellen, deterministischen Baseline-Lösung.
