(ns mynamespace.ProyectoSemaforoT
  (:require [clojure.java.io :as io]))




;; Se declaran las funciones para obtener los colores de los semaforos

;;Se declara la funcion assoc la cual hace que se pueda buscar un valor en una lista de y se regresa el par de la lista
(defn myassoc [key lista]
  (cond
    (empty? lista) false
    (= key (first (first lista))) (first lista)
    :else (myassoc key (rest lista))))

(defn inicio-rojo [lista]
  (first (second (myassoc 'rojo lista))))

(defn fin-rojo [lista]
  (second (second (myassoc 'rojo lista))))

(defn return-menor [a b]
  (if (< a b) a b))

(defn return-mayor [a b]
  (if (> a b) a b))

(defn inicio-verde [lista]
  (return-menor (first (second (myassoc 'verde lista))) (first (second (myassoc 'vuelta lista)))))

(defn fin-verde [lista]
  (return-mayor (second (second (myassoc 'verde lista))) (second (second (myassoc 'vuelta lista)))))

(defn inicio-amarillo [lista]
  (first (second (myassoc 'amarillo lista))))

(defn fin-amarillo [lista]
  (second (second (myassoc 'amarillo lista))))

(defn tiempo-semaforo [lista]
  (apply max (flatten (map second lista))))


(defn sacar-tiempos-de-colores [lista]
  (map second lista))

(defn lista-nombre-color [lista]
  (map first lista))

(defn nombre-colores [nombre lista]
   (list (list (first nombre) (first lista))
   (list (second nombre) (second lista))
   (list (nth nombre 2) (nth lista 2))
   (list (nth nombre 3) (nth lista 3))
   )
  )



(nombre-colores '(rojo verde vuelta amarillo) '((0 10) (10 15) (15 16) (16 19)))


;;Recibe una lista con los tiempos , y da el inicio y final de cada uno

(defn colores-aux [lista outLista]
  (if (empty? lista)
    (drop 2 (reverse outLista))
    (colores-aux (rest lista)
           (concat (list (list (second (first outLista)) (+ (second (first outLista)) (first lista)))) outLista))))


(defn colores-aux-2 [lista]
    (nombre-colores (lista-nombre-color lista) 
  (colores-aux (cons 0 (sacar-tiempos-de-colores lista)) '((0 0)))))


;;Ocupando myassoc logro de una manera tener un orden constiente en los colores de cualquier tipo de semaforo
(defn colores [lista]
  (if (empty? lista)
    '()
    (concat (list (colores-aux-2 (first lista))) (colores (rest lista)))))


; Funcion para calcular el tiempo de espera individual de cada vehiculo 

(defn tiempo-pasar-individual [tiempo colores-tiempo]
  (cond 
    (> (quot tiempo (tiempo-semaforo colores-tiempo)) 0)
    (recur (- tiempo (* (tiempo-semaforo colores-tiempo) (quot tiempo (tiempo-semaforo colores-tiempo)))) colores-tiempo)
    :else
    (cond
      (and (>= tiempo (inicio-rojo colores-tiempo)) (< tiempo (fin-rojo colores-tiempo)))
      (- (fin-rojo colores-tiempo) tiempo)
      (and (>= tiempo (inicio-verde colores-tiempo)) (< tiempo (fin-verde colores-tiempo)))
      0
      (and (>= tiempo (inicio-amarillo colores-tiempo)) (< tiempo (fin-amarillo colores-tiempo))) 
      (+ (- (fin-amarillo colores-tiempo) tiempo) (- (fin-rojo colores-tiempo) (inicio-rojo colores-tiempo))))))
  
  ; Función para calcular el tiempo de salida de un vehiculo tomando en cuenta los dos segundos que tarda un vehiculo en pasar



(defn tiempos-salida [tiempos lista-tiempos-salida i-verde final-verde tiempo-semaforo colores-semaforo]
  (cond
    (empty? tiempos) (reverse lista-tiempos-salida)
    (> (first lista-tiempos-salida) final-verde)
    (tiempos-salida tiempos lista-tiempos-salida (+ (inicio-verde colores-semaforo) 
                                                   (* (quot (first lista-tiempos-salida) tiempo-semaforo) 
                                                      tiempo-semaforo)) 
                     (+ (fin-verde colores-semaforo) 
                        (* (quot (first lista-tiempos-salida) tiempo-semaforo) tiempo-semaforo)) 
                     tiempo-semaforo colores-semaforo)
    (<= (first tiempos) (first lista-tiempos-salida))
    (if (< (+ (first lista-tiempos-salida) 2) final-verde)
      (tiempos-salida (rest tiempos) 
                      (cons (+ 2 (first lista-tiempos-salida)) lista-tiempos-salida) 
                      i-verde final-verde tiempo-semaforo colores-semaforo)
      (tiempos-salida (rest tiempos) 
                      (cons (+ i-verde tiempo-semaforo) lista-tiempos-salida) 
                      (+ i-verde tiempo-semaforo) 
                      (+ final-verde tiempo-semaforo) tiempo-semaforo colores-semaforo))
    :else
    (tiempos-salida (rest tiempos) 
                    (cons (+ (tiempo-pasar-individual (first tiempos) colores-semaforo) 
                             (first tiempos)) lista-tiempos-salida) 
                    (+ (inicio-verde colores-semaforo) 
                       (* (quot (first tiempos) tiempo-semaforo) tiempo-semaforo)) 
                    (+ (fin-verde colores-semaforo) 
                       (* (quot (first tiempos) tiempo-semaforo) tiempo-semaforo)) 
                    tiempo-semaforo colores-semaforo)))

;;Funcion auxiliar del tiempo salida
(defn tiempo-salida-primero [tiempos inicio-verde final-verde tiempo-semaforo colores-semaforo]
  (if (empty? tiempos)
    '()
    (tiempos-salida (rest tiempos) 
                    [(+ (tiempo-pasar-individual (first tiempos) colores-semaforo) 
                        (first tiempos))] 
                    inicio-verde final-verde tiempo-semaforo colores-semaforo)))

;; hace la funcion de arriba para cada carril del semaforo
(defn tiempo-salida-recursivo [tiempos inicio-verde final-verde tiempo-semaforo colores-semaforo]
  (if (empty? tiempos)
    '()
    (if (sequential? (first tiempos))
      (cons (tiempo-salida-primero (first tiempos) inicio-verde final-verde tiempo-semaforo colores-semaforo) 
            (tiempo-salida-recursivo (rest tiempos) inicio-verde final-verde tiempo-semaforo colores-semaforo)))))

  ;Función que me va a hacer el tiempo de salida para cada semaforo
(defn genera-tiempos-salida [nSemaforos nSemaforo datos colores-semaforo]
  (if (zero? nSemaforos)
    '()
    (cons (tiempo-salida-recursivo (first datos)
                                   (inicio-verde (first colores-semaforo))
                                   (fin-verde (first colores-semaforo))
                                   (tiempo-semaforo (first colores-semaforo))
                                   (first colores-semaforo))
          (genera-tiempos-salida (dec nSemaforos)
                                 (inc nSemaforo)
                                 (rest datos)
                                 (rest colores-semaforo)))))

  ;;Crea un lista con los tiempos de salida de cada carro
(defn restar-datos [lista1 lista2]
  (map (fn [sublist1 sublist2] 
         (map (fn [sub-sublist1 sub-sublist2] 
                (map - sub-sublist1 sub-sublist2)) 
              sublist1 sublist2)) 
       lista1 lista2))


  ;; Con estas dos funcion sacamos el promedio de los tiempos de cada semaforo
(defn promedio-por-semaforos [datos n]
  (cond
    (empty? datos) 0
    (seq? (first datos)) (+ (/ (/ (apply + (first datos)) (count (first datos))) n) 
                             (promedio-por-semaforos (rest datos) n))
    )
  )

(defn promedio-tiempos [nSemaforos nSemaforo datos]
  (cond
    (= nSemaforos 0) '()
    (empty? datos) '()
    (seq? (first datos)) (concat (list (double (promedio-por-semaforos (first datos) (count (first datos)))))
                                  (promedio-tiempos (dec nSemaforos) (inc nSemaforo) (rest datos)))))



(defn imprimir-promedio-semaforos [lista nSemaforos]
  (if (empty? (rest lista))
    (do
      (print "Promedio de tiempo de espera en el semaforo ")
      (println (- nSemaforos (count lista)))
      (println (first lista)))
    (do
      (print "Promedio de tiempo de espera en el semaforo ")
      (println (- nSemaforos (count lista)))
      (println (first lista))
      (imprimir-promedio-semaforos (rest lista) nSemaforos))))


;;Funciones para imprimir los promedios
(defn promedio-total [lista]
  (println "Promedio del tiempo de espera en el crucero: ")
  (println (/ (apply + lista) (count lista)))
  (imprimir-promedio-semaforos lista (inc (count lista))))


  ;; Con estas dos funciones sacamos el numero de autos
  ;;Recursion terminal
(defn num-autos-por-semaforo [lista]
  (if (empty? lista)
    0
    (if (sequential? (first lista))
      (+ (count (first lista))
         (num-autos-por-semaforo (rest lista))))))


(defn num-autos [datos nSemaforos nSemaforo sumTotal]
  (if (empty? datos)
    (do
      (println "Total de autos que pasaron por el crucero:")
      (println sumTotal))
    (if (sequential? (first datos))
      (do
        (print "Numero de autos que pasaron por el semaforo ")
        (println nSemaforo)
        (println (num-autos-por-semaforo (first datos)))
        (num-autos (rest datos) (dec nSemaforos) 
                   (inc nSemaforo) 
                   (+ sumTotal (num-autos-por-semaforo (first datos))))))))


  ;;Con estas funciones obtenemos el numero de tiempos muertos


(defn crear-lista-salida [lista1 lista2]
  (map (fn [sublist1 sublist2] 
         (map (fn [sub-sublist1 sub-sublist2] 
                (map + sub-sublist1 sub-sublist2)) 
              sublist1 sublist2)) 
       lista1 lista2))

(defn aplanar-un-nivel-lista [lista]
  (map #(apply concat %) lista))

(defn quicksort [lista]
  (if (empty? lista)
    '()
    (if (empty? (rest lista))
      lista
      (concat (quicksort (filter #(> (first lista) %) lista))
              (filter #(= (first lista) %) lista)
              (quicksort (filter #(< (first lista) %) lista))))))

(defn quicksort-pair [lista]
  (if (empty? lista)
    '()
    (if (empty? (rest lista))
      lista
      (concat (quicksort-pair (filter #(> (second (first lista)) (second %)) lista))
              (filter #(= (second (first lista)) (second %)) lista)
              (quicksort-pair (filter #(< (second (first lista)) (second %)) lista))))))


(defn q-lista-salida [lista]
  (map quicksort lista))

(defn tiempo-muerto-semaforo [lista inicio-verde fin-verde tiempo-semaforo]
  (cond
    (empty? lista) 0
    (and (>= (first lista) inicio-verde) (< (first lista) fin-verde))
    (tiempo-muerto-semaforo (rest lista) 
                            (+ inicio-verde tiempo-semaforo) 
                            (+ fin-verde tiempo-semaforo) 
                            tiempo-semaforo)
    (>= (first lista) fin-verde)
    (inc (tiempo-muerto-semaforo lista 
                                 (+ inicio-verde tiempo-semaforo) 
                                 (+ fin-verde tiempo-semaforo) 
                                 tiempo-semaforo))
    :else
    (tiempo-muerto-semaforo (rest lista) 
                            inicio-verde 
                            fin-verde 
                            tiempo-semaforo)))




(defn tiempo-muerto-crucero [datos crucero-lista]
  (if (empty? datos)
    '()
    (cons (tiempo-muerto-semaforo (first datos) 
                                  (inicio-verde (first crucero-lista)) 
                                  (fin-verde (first crucero-lista)) 
                                  (tiempo-semaforo (first crucero-lista))) 
          (tiempo-muerto-crucero (rest datos) (rest crucero-lista)))))

(defn imprimir-tiempo-muerto-semaforos [lista nSemaforos]
  (if (empty? (rest lista))
    (do
      (print "Tiempos muertos en el semaforo ")
      (println (- nSemaforos (count lista)))
      (println (first lista)))
    (do
      (print "Tiempos muertos en el semaforo ")
      (println (- nSemaforos (count lista)))
      (println (first lista))
      (imprimir-tiempo-muerto-semaforos (rest lista) nSemaforos))))

(defn tiempo-muerto-total [lista]
  (println "Veces de tiempo muerto en todos los semaforos:")
  (println (apply + lista))
  (imprimir-tiempo-muerto-semaforos lista (inc (count lista))))

(defn obtener-tiempo-muerto [colores lista-tiempo-salida]
  (tiempo-muerto-total (tiempo-muerto-crucero (q-lista-salida (aplanar-un-nivel-lista lista-tiempo-salida)) colores)))


(defn average [lista]
  (/ (apply + lista) (count lista)))

;; 
;; 
;; 
;; Paralelización
;; 
;; 
;; 

(defn nombre-config [n]
  (str "config" n ".txt"))

(defn nombre-datos [n]
  (str "data" n ".txt"))

(defn nombre-out [n]
  (str "out" n ".txt"))

(defn nombre-crucero [n]
  (str "Crucero " n))


;;Creamos la funcion que lee el archivo y guarda los contenidos en una lista

(defn read-list-from-file [filename]
  (with-open [rdr (io/reader filename)]
    (read (java.io.PushbackReader. rdr))))

;;Funcion para imprimir lo que esta pasando

(defn letreros-verde [i-verde f-verde tiempo-semaforo end-time n-crucero]
  (if (< f-verde end-time)
    (do 
      (println (nombre-crucero n-crucero) " Semaforo en verde a " i-verde)
      (letreros-verde (+ i-verde tiempo-semaforo) (+ f-verde tiempo-semaforo) tiempo-semaforo end-time n-crucero))
    (do 
      (println (nombre-crucero n-crucero) " Semaforo en verde a " i-verde)
    )))

;; Regresa el maximo de una lista
(defn get-max [lista]
  (apply max (apply concat lista))
  )

;;Funcion que imprime los letreros para cada semaforo
(defn letrero-recursivo [colores-crucero datos n-crucero]
  (if (empty? colores-crucero)
    '()
    (do
      (letreros-verde (inicio-verde (first colores-crucero)) (fin-verde (first colores-crucero)) (tiempo-semaforo (first colores-crucero)) (get-max (first datos)) n-crucero)
      (letrero-recursivo (rest colores-crucero) (rest datos) n-crucero)
      )
    )
  )

;;Funcion que analiza un crucero

(defn -main2 [n]
  (let [archivoConfig (nombre-config n)
        archivoData (nombre-datos n)
        crucero-lista (first (read-list-from-file archivoConfig))
        datos (read-list-from-file archivoData)
        colores-semaforo (colores crucero-lista)
        lista-tiempo-salida (genera-tiempos-salida (count colores-semaforo) 1 datos colores-semaforo)
        lista-tiempo-espera2 (restar-datos lista-tiempo-salida datos)
        ;; lista-tiempos-muertos (tiempo-muerto-crucero (q-lista-salida (aplanar-un-nivel-lista lista-tiempo-salida)) colores-semaforo)
        lista-promedios-tiempo-espera (promedio-tiempos (count colores-semaforo) 1 lista-tiempo-espera2)
        archivoOut (nombre-out n)]

    (with-open [writer (clojure.java.io/writer (str "letreros" n ".txt"))]
      (binding [*out* writer]
    (letrero-recursivo colores-semaforo lista-tiempo-salida n)
        ))

    (with-open [writer (clojure.java.io/writer archivoOut)]
      (binding [*out* writer]
        (num-autos datos (count colores-semaforo) 1 0)
        (println "")
        (obtener-tiempo-muerto colores-semaforo lista-tiempo-salida)
        (println "")
        (promedio-total lista-promedios-tiempo-espera)))))


;;Función que busca dentro los outs por un dato en especifico

(def buscar-numero
  (fn [linea nArchivo]
    (with-open [archivo (io/reader nArchivo)]
      (let [lineas (line-seq archivo)
            indiceLinea
            (some #(if (re-find (re-pattern linea) (second %)) (first %) nil)
                  (map-indexed vector lineas))]
        (if indiceLinea
          (Float/parseFloat (nth lineas (inc indiceLinea)))
          nil)))))
;;Funciones que ocupan buscar-numero para obtener los datos de los cruceros
(def buscar-total-autos
  (fn [nArchivo]
    (buscar-numero "Total de autos que pasaron por el crucero:" nArchivo)))

(def buscar-tiempo-espera
  (fn [nArchivo]
    (buscar-numero "Promedio del tiempo de espera en el crucero:" nArchivo)))


(def buscar-tiempo-muerto-semaforos
  (fn [n]
    (map #(concat % (list n)) (map #(cons % (list (buscar-numero (str "Tiempos muertos en el semaforo " %) (nombre-out n)))) (range 1 (inc (count (first (read-list-from-file (nombre-config n))))))))))


;;NUM AUTOS
;;Imprime la cantiad de autos que pasaron por cada crucero
(def mostrar-autos-cruceros
  (fn [lista]
    (dorun (map #(do
                   (println (nombre-crucero %))
                   (println (str "Semaforos: " (count (first (read-list-from-file (nombre-config %))))))
                   (println "Cantidad de vehículos: " (buscar-total-autos (nombre-out %)))
                   (println " ")) lista))))

;;Imprime el tiempo promedio de espera de todos los cruceros analizados
(def tiempo-promedio-espera-cruceros
  (fn [lista]
    (average (map #(buscar-tiempo-espera (nombre-out %)) lista))))

;;Función para imprimir el contenido del archivo letreros

(defn print-file-contents-letreros [n]
  (let [contents (slurp (str "letreros" n ".txt"))]
    (println " ")
    (println contents)))

;;Funcion para imprimir el contenido de un archivo out
(defn print-file-contents [n]
  (let [contents (slurp (nombre-out n))]
    (println "Información del Crucero " n)
    (println " ")
    (println contents)))


;;TIEMPO ESPERA
;;Función que obtiene todos los TE de los cruceros y los ordena para sacar el 10% más alto y más bajo
(def obtener-lista-cruceros-tiempo-espera
  (fn [lista-cruceros]
    (quicksort-pair (map #(concat (list %)  (list (buscar-tiempo-espera (nombre-out %)))) lista-cruceros)))
)
;;Función que imprime el 10% TE más bajo
(def imprime-10%-mas-bajo
  (fn [lista]
    (let [porciento10 (int (* 0.1 (count lista)))
          porciento10 (if
                       (zero? porciento10) 1 porciento10)
          listaPorciento10 (take porciento10 lista)]

      (dorun (map #(do
                     (println (nombre-crucero (first %)))
                     (print "Tiempo de espera: ")
                     (println (second %))
                     (println " ")) listaPorciento10)))))
;;Función que imprime el 10% TE más alto

(def imprime-10%-mas-alto
  (fn [lista]
    (let [lista (reverse lista)
          porciento10 (int (* 0.1 (count lista)))
          porciento10 (if
                       (zero? porciento10) 1 porciento10)
          listaPorciento10 (take porciento10 lista)]

      (dorun (map #(do
                     (println (nombre-crucero (first %)))
                     (print "Tiempo de espera: ")
                     (println (second %))
                     (println " ")) listaPorciento10)))))


;;Función para imprimir ambos TE
(def imprime-ambos-10%-tiempo-espera
  (fn [lista]
    (let [lista2 (obtener-lista-cruceros-tiempo-espera lista)]
      (println "10% de cruceros con mayor tiempo de espera:")
      (imprime-10%-mas-alto lista2)
      (println "10% de cruceros con menor tiempo de espera:")
      (imprime-10%-mas-bajo lista2))))


;;TIEMPO MUERTO

;;Función que imprme el 10% de los semaforos con mayor tiempo muerto
(def imprime-10%-mas-alto-semaforo-crucero-tiempo-muerto
  (fn [lista]
    (let [lista (reverse lista)
          porciento10 (int (* 0.1 (count lista)))
          porciento10 (if
                       (zero? porciento10) 1 porciento10)
          listaPorciento10 (take porciento10 lista)]

      (dorun (map #(do
                     (println (nombre-crucero (nth % 2)))
                     (println "Semaforo:" (first %))
                     (print "Tiempo muerto:" (second %))
                     (println " ")
                     (println " ")) listaPorciento10)))))


;;Función que obtiene los tiempos muertos de los semaforos de los cruceros y los ordena para imprimir el 10% más alto

(def obtener-lista-cruceros-tiempo-muerto
  (fn [lista-cruceros]
    (quicksort-pair (apply concat (map #(buscar-tiempo-muerto-semaforos %) lista-cruceros)))))



;;Función que imprime el 10% de los semaforos con mayor tiempo muerto
(def imprime-10%-mas-alto-tiempo-muerto
  (fn [lista]
    (let [lista2 (obtener-lista-cruceros-tiempo-muerto lista)]
      (println "10% de semaforos con mayor tiempo de muerto:")
      (imprime-10%-mas-alto-semaforo-crucero-tiempo-muerto lista2))))

;;Función que sirve para recibir el input y guardarlo como una lista
(defn read-numbers []
  (let [line (read-line)
        strings (clojure.string/split line #"\s+")
        numbers (map #(Integer/parseInt %) strings)]
    numbers))
;;Función que elimina los archivos letreros, recibe una lista con una string del nombre
(defn delete-files [files]
  (doseq [file files]
    (java.nio.file.Files/deleteIfExists (java.nio.file.Paths/get file (into-array String [])))))

;;Función main Final
(defn -mainFinal []
  (println "Ingrese el id de los cruceros a analizar:")
  (def lista-cruceros (read-numbers))
  (println "Analizando " lista-cruceros "...")
  (doall (pmap #(doall (map -main2 %)) (partition-all 4 lista-cruceros)))
  (doall (map #(print-file-contents-letreros %) lista-cruceros))
  (doall (delete-files (doall(map #(str "letreros" % ".txt") lista-cruceros))))
  (println "Analisis completado")
  (println "Ingrese el id de los cruceros cuyas estadisticas quieras mostrar:")
  (def lista-cruceros-mostrar (read-numbers))
  (doall (map #(print-file-contents %) lista-cruceros-mostrar))
  (println "Estadisticas de los cruceros analizados: ")
  (println " ")
  (println "Cantidad de vehiculos que pasaron por los cruceros:")
  (mostrar-autos-cruceros lista-cruceros)
  (println " ")
  (println "Tiempo promedio de espera de un vehículo en todos los cruceros:")
  (println (tiempo-promedio-espera-cruceros lista-cruceros))
  (println " ")
  (println "Cruceros con mayor y menor tiempo de espera:")
  (imprime-ambos-10%-tiempo-espera lista-cruceros)
  (println " ")
  (println "Semaforos con mayor tiempo muerto:")
  (imprime-10%-mas-alto-tiempo-muerto lista-cruceros)

  ;;Crea el archivo outFinal.txt

  (with-open [writer (clojure.java.io/writer "outFinal.txt")]
      (binding [*out* writer]
  (doall (map #(print-file-contents %) lista-cruceros-mostrar))
  (println "Estadisticas de los cruceros analizados: ")
  (println " ")
  (println "Cantidad de vehiculos que pasaron por los cruceros:")
  (mostrar-autos-cruceros lista-cruceros)
  (println " ")
  (println "Tiempo promedio de espera de un vehículo en todos los cruceros:")
  (println (tiempo-promedio-espera-cruceros lista-cruceros))
  (println " ")
  (println "Cruceros con mayor y menor tiempo de espera:")
  (imprime-ambos-10%-tiempo-espera lista-cruceros)
  (println " ")
  (println "Semaforos con mayor tiempo muerto:")    
  (imprime-10%-mas-alto-tiempo-muerto lista-cruceros)
        ))
  )


(time (-mainFinal))
  
(doall (delete-files (map #(str "letreros" % ".txt") (range 11 101))))
(doall (delete-files (map #(str "out" % ".txt") (range 1 101))))

;; 

  (time (pmap #(map -main2 %) (partition 3 '(1 2))))

  (def lista-cruceros2 (range 1 11))


  (time (dorun (pmap #(doall (map -main2 %)) (partition-all 4 lista-cruceros))))

  
  (time (dorun (map #(doall (-main2 %)) lista-cruceros)))


(defn enlista [lista]
(if (empty? lista) () 
  (concat (list (list (first lista))) (enlista (rest lista))))
  )


(enlista '(1 2 3))
  
(defn examen [f g] (fn [x] (f (g x))))

(def función (examen (fn [x] (* x x)) inc))


(función 2)