# Define las listas como strings
tiempos = """(
(
(1 2 3 4 5 6 7 1 9 34)
(1 2 3 4 5 6 7 1 9)
(1 2 3 4 5 6 7 1 9 53)
(1 2 3 4 5 6 7 1 9 72)
(1 2 3 4 5 6 7 1 9)
)
(
(1 2 3 4 5 6 7 2 9)
(1 2 3 4 5 6 7 2 9)
(1 2 3 4 5 6 7 2 9)
(1 2 3 4 5 6 7 2 9)
)
(
(1 2 3 4 5 6 7 9)
(1 2 3 4 5 6 7 3 9)
(1 2 3 4 5 6 7 3 9)
(1 2 3 4 5 6 7 3 9)
)
(
(1 2 3 4 5 6 7 4 9)
(1 2 3 4 5 6 7 4 9)
(1 2 3 4 5 6 7 4 9)
(1 2 3 4 5 6 7 4 9 10 100)
)
(
(1 2 3 4 5 6 7 4 9)
(1 2 3 4 5 6 7 4 9)
(1 2 3 4 5 6 7 4 9)
(1 2 3 4 5 6 7 4 9 10)
)
)"""

crucero = """(
(
    ((rojo 10) (verde 5) (vuelta 1) (amarillo 3)) 
    ((rojo 10) (verde 5) (vuelta 1) (amarillo 8)) 
    ((rojo 10) (verde 5) (vuelta 1) (amarillo 9)) 
    ((verde 10) (vuelta 5) (amarillo 1) (rojo 1)) 
    ((rojo 10) (verde 5) (vuelta 1) (amarillo 1))
)
(
    (verde 10 rojo 9)
    (verde 10 rojo 9)
    (verde 10 rojo 9)
    (verde 10 rojo 9)
)
)"""

# Guarda las listas en 10 archivos de texto para cada lista
for i in range(7, 101):
    with open(f"data{i}.txt", "w") as f:
        f.write(tiempos)

    with open(f"config{i}.txt", "w") as f:
        f.write(crucero)


# Crear una lista del 1 al 100
lista = list(range(1, 101))

# Imprimir la lista
print(lista)
