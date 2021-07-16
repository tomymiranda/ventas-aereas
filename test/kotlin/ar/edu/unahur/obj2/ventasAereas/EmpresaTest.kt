package ar.edu.unahur.obj2.ventasAereas

import io.kotest.assertions.throwables.shouldThrowAny

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.time.LocalDate


class EmpresaTest: DescribeSpec({

    val aerolineasMym = Empresa(Segura, Estricta)

    val buenosAires = Ciudad("Buenos Aires",AmericaDelSur)
    val miami = Ciudad("Miami",AmericaDelNorte)
    val bahamas = Ciudad("Las Bahamas",AmericaCentral)
    val moscu = Ciudad("Moscu",Europa)
    val bariloche = Ciudad("Bariloche", AmericaDelSur)

    val avionAerolinea = Avion(120, 30.0, 500.0)
    val avionAerolinea2 = Avion(30, 30.0, 600.0)

    val vueloALosAngeles = VueloCharter(LocalDate.now(), avionAerolinea, buenosAires, bahamas, precioEstandar = 15000.0)
    val vueloAMiami = VueloPasajero(LocalDate.now(), avionAerolinea, buenosAires, miami, precioEstandar = 8000.0)
    val vueloMoscu = VueloCarga(LocalDate.of(2021, 8, 21), avionAerolinea2, buenosAires, moscu, precioEstandar = 3000.0, 900.0)

    describe("tercer requerimiento"){
        // El avión tiene 10 plazas y se venden 7 para Moscú
        aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 1)
        aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 2)
        aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 3)
        aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 4)
        aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 5)
        aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 6)
        aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 7)

        // Pruebas del 3er requerimiento
        it("La empresa no puede vender pasajes para el vuelo de Moscú porque no es seguro") {
            // el vuelo de Moscú todavía es de venta Segura
            aerolineasMym.puedeVenderPorCriterio(vueloMoscu).shouldBeTrue()
            aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 8)
            // el vuelo de Moscú deja de ser de venta Segura
            aerolineasMym.puedeVenderPorCriterio(vueloMoscu).shouldBeFalse()
        }

        it("Cambia la política de venta y puede vender el pasaje") {
            aerolineasMym.puedeVenderPorCriterio(vueloMoscu).shouldBeTrue()
            aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 8)
            aerolineasMym.cambiarCriterio(LaxaFija)
            // Puede vender el pasaje por cambio de política
            aerolineasMym.puedeVenderPorCriterio(vueloMoscu).shouldBeTrue()
        }
        it("Cambia el criterio laxoporcentual y puede vender hasta un pasaje más del límite") {
            aerolineasMym.cambiarCriterio(LaxaPorcentual)
            aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 8)
            aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 9)
            aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 10)
            aerolineasMym.puedeVenderPorCriterio(vueloMoscu).shouldBeTrue()
            aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 11)
            aerolineasMym.puedeVenderPorCriterio(vueloMoscu).shouldBeFalse()
        }
        it("Por pandemia se suspende la venta de pasajes") {
            aerolineasMym.puedeVenderPorCriterio(vueloMoscu).shouldBeTrue()
            aerolineasMym.cambiarCriterio(Pandemia)
            aerolineasMym.puedeVenderPorCriterio(vueloMoscu).shouldBeFalse()
        }
    }
    describe("cuarto requerimiento"){
        it("El precio del vuelo a Miami, con política estricta, vale igual que el precio estándar") {
            aerolineasMym.precioDeVenta(vueloAMiami).shouldBe(8000.0)
        }

        it("Se cambia la política a venta anticipada y el precio a Los Ángeles cambia a 4500.0") {
            // La política previa es estricta
            aerolineasMym.precioDeVenta(vueloALosAngeles).shouldBe(15000.0)
            aerolineasMym.cambiarPolitica(VentaAnticipada)
            aerolineasMym.precioDeVenta(vueloALosAngeles).shouldBe(4500.0)
        }

        it("El precio a Los Ángeles, entre 40 y 79 pasajeros, es de 9000.0 ") {
            (1..45).forEach {
                aerolineasMym.venderPasaje(vueloALosAngeles, LocalDate.now(), it)
            }
            aerolineasMym.cambiarPolitica(VentaAnticipada)
            aerolineasMym.precioDeVenta(vueloALosAngeles).shouldBe(9000.0)
        }

        it("El vuelo a Los Angeles con más de 79 pasajeros vende sus pasajes al precio estándar") {
            (1..85).forEach {
                aerolineasMym.venderPasaje(vueloALosAngeles, LocalDate.now(), it)
            }
            VentaAnticipada.precioConElQueSeVende(vueloALosAngeles).shouldBe(15000.0)
            aerolineasMym.precioDeVenta(vueloALosAngeles).shouldBe(15000.0)
        }

        it("Se cambia la política a remate y el vuelo al tener menos de 30 pasajeros, se vende los pasajes a 2000.0 ") {
            aerolineasMym.cambiarPolitica(Remate)
            aerolineasMym.precioDeVenta(vueloAMiami).shouldBe(2000.0)
        }

        it("Con política de remate y el vuelo con menos de 30 asientos libres, los pasajes valen la mitad del precio estandar") {
            (1..90).forEach { aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), it) }
            aerolineasMym.cambiarPolitica(Remate)
            // El precio estándar es 8000
            aerolineasMym.precioDeVenta(vueloAMiami).shouldBe(4000.0)
        }
    }
    describe("quinto requermiento"){
        it("se venden 3 pasajes y entonces se registran 3 pasajes") {
            aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), 10)
            aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), 11)
            aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), 12)

            vueloAMiami.cantidadDeAsientosOcupados().shouldBe(3)
        }

        it("No se puede vender más pasajes y lanza error") {
            (1..7).forEach { aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), it) }

            vueloMoscu.cantidadDeAsientosLibres().shouldBe(3)
            aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 8)
            vueloMoscu.cantidadDeAsientosLibres().shouldBe(2)
            shouldThrowAny { aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 9) }
        }
    }
    describe("sexto y septimo requerimientos"){
        // El avión tiene 10 plazas y se venden 7 para Moscú
        aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 1)
        aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 2)
        aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 3)
        aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 4)
        aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 5)
        aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 6)
        aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 7)

        describe("sexto requerimiento"){

            // pruebas del 6to requerimiento
            it("el precio acumulado de los pasajes vendido para el vuelo a moscu debe ser 21000.0"){
                aerolineasMym.importeTotalGeneradoParaUnVuelo(vueloMoscu).shouldBe(21000.0)
            }
            it("se cambian las politica de venta de la empresa y eso influye en el acumulado al vender un vuelo"){
                aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), 1)
                aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), 2)
                aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), 3)

                aerolineasMym.cambiarPolitica(VentaAnticipada)

                aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), 4)
                aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), 5)
                aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), 6)

                aerolineasMym.cambiarPolitica(Remate)

                aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), 7)
                aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), 8)
                aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), 9)

                aerolineasMym.importeTotalGeneradoParaUnVuelo(vueloAMiami).shouldBe(37200.0)
            }

        }

        describe("septimo requerimiento"){
            //pruebas del 7mo requerimiento, deberian ser 3 creo
            it("el peso del vuelo a moscu,que es un vuelo de cargar, es 2340 "){
                avionAerolinea2.pesoDelAvion.shouldBe(600.0)
                vueloMoscu.pesoPorLaCantidadDePasajerosEnElVuelo().shouldBe(140.0)
                vueloMoscu.pesoDeCarga(aerolineasMym).shouldBe(1600.0)
                vueloMoscu.pesoDelVuelo(aerolineasMym).shouldBe(2340.0)
            }
            it("el peso del vuelo a miami, que es un vuelo de pasajeros, es de 590"){
                aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), 1)
                aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), 2)
                aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), 3)

                vueloAMiami.pesoDelVuelo(aerolineasMym).shouldBe(590)

            }
            it("el peso del vuelo a los angeles, que es un vuelo charter, es de 5560 "){
                aerolineasMym.venderPasaje(vueloALosAngeles, LocalDate.now(), 1)
                aerolineasMym.venderPasaje(vueloALosAngeles, LocalDate.now(), 2)
                aerolineasMym.venderPasaje(vueloALosAngeles, LocalDate.now(), 3)

                vueloALosAngeles.pesoDelVuelo(aerolineasMym).shouldBe(5560)
            }
        }
    }

    // Bonus 3 (Carlos A. Martín) -->
    describe("Registro de pagos (Bonus 3)") {
        val boing707 = Avion(80, 15.73, 14756.0)
        val barilocheVacaciones = VueloCharter(LocalDate.of(2021, 1, 3), boing707, buenosAires, bariloche, 6999.99)
        aerolineasMym.programarVuelo(barilocheVacaciones)

        aerolineasMym.venderPasaje(barilocheVacaciones, LocalDate.now(), 123456789)
        aerolineasMym.cobrarPasaje(barilocheVacaciones, 123456789, 3000.0)

        it("El pasajero debe $ 3999.99") {
            aerolineasMym.cuantoDebe(123456789).shouldBe(3999.99)
        }

        val barilocheComercial = VueloCharter(LocalDate.of(2021, 9, 12), boing707, buenosAires, bariloche, 4699.99)
        aerolineasMym.programarVuelo(barilocheComercial)
        aerolineasMym.venderPasaje(barilocheComercial, LocalDate.now(), 123456789)

        it("El mismo pasajero compra un pasaje para otro vuelo pero no lo abona. Debe 8699.98") {
            aerolineasMym.cuantoDebe(123456789).shouldBe(8699.98)
        }
    }
    // <-- Prueba Bonus 3 (Carlos A. Martín)
})