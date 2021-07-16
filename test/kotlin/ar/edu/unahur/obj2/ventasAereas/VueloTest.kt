package ar.edu.unahur.obj2.ventasAereas

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class VueloTest : DescribeSpec({

  val hoy = LocalDate.now()

  val aerolineasMym = Empresa(Segura, Estricta)

  val buenosAires = Ciudad("Buenos Aires", AmericaDelSur)
  val bariloche = Ciudad("Bariloche", AmericaDelSur)
  val mendoza = Ciudad("Mendoza", AmericaDelSur)
  val moscu = Ciudad("Moscu", Europa)
  val miami = Ciudad("Miami", AmericaDelNorte)
  val bahamas = Ciudad("Las Bahamas", AmericaCentral)


  val avioneta = Avion(200, 10.0, 200.0)
  val avionAerolinea = Avion(30, 30.0, 600.0)
  val avionPrivado = Avion(60, 25.0, 700.0)
  val boing707 = Avion(80, 15.73, 14756.0)

  val vueloAMiami = VueloPasajero(LocalDate.now(), avioneta, buenosAires, miami, precioEstandar = 8000.0)
  val vueloMoscu = VueloCarga(LocalDate.now(), avionAerolinea, buenosAires, moscu, precioEstandar = 3000.0, 800.0)
  val vueloBahamas = VueloCharter(LocalDate.now(), avionPrivado, buenosAires, bahamas, precioEstandar = 10000.0)
  val barilocheMayo = VueloCharter(LocalDate.of(2021, 5, 28), boing707, buenosAires, bariloche, 4900.99)
  val barilocheJunio = VueloCharter(LocalDate.of(2021, 6, 14), boing707, buenosAires, bariloche, 5900.99)
  val barilocheJulio = VueloCharter(LocalDate.of(2021, 7, 7), boing707, buenosAires, bariloche, 6900.99)
  val barilocheAgosto = VueloCharter(LocalDate.of(2021, 8, 21), boing707, buenosAires, bariloche, 6100.99)
  val barilocheSeptiembre = VueloCharter(LocalDate.of(2021, 9, 14), boing707, buenosAires, bariloche, 5500.99)
  val mendozaJulio = VueloCharter(LocalDate.of(2021, 7, 7), boing707, buenosAires, mendoza, 5928.99)
  val vueloBarilocheHoy = VueloCharter(LocalDate.now(), boing707, buenosAires, bariloche, 6100.99)

  describe("primer requerimiento"){

    it("la cantidad de asientos libres del vuelo a Moscú es 9") {
      vueloMoscu.cantidadDeAsientosDisponibles().shouldBe(10)
      vueloMoscu.cantidadDeAsientosOcupados().shouldBe(0)

      aerolineasMym.venderPasaje(vueloMoscu, LocalDate.now(), 12345678)

      vueloMoscu.cantidadDeAsientosOcupados().shouldBe(1)
      vueloMoscu.cantidadDeAsientosLibres().shouldBe(9)
    }
    it("la cantidad de asientos libres para el vuelo charter de las bahamas es de 32") {
      vueloBahamas.cantidadDeAsientosDisponibles().shouldBe(35)

      aerolineasMym.venderPasaje(vueloBahamas, LocalDate.now(), 12345677)
      aerolineasMym.venderPasaje(vueloBahamas, LocalDate.now(), 12345678)
      aerolineasMym.venderPasaje(vueloBahamas, LocalDate.now(), 12345679)

      vueloBahamas.cantidadDeAsientosOcupados().shouldBe(3)
      vueloBahamas.cantidadDeAsientosLibres().shouldBe(32)
    }

    it("la cantidad de asientos libres para el vuelo de pasajeros a miami es de 50") {
      vueloAMiami.cantidadDeAsientosLibres().shouldBe(200)

      (1..150).forEach { aerolineasMym.venderPasaje(vueloAMiami, LocalDate.now(), it) }

      vueloAMiami.cantidadDeAsientosOcupados().shouldBe(150)
      vueloAMiami.cantidadDeAsientosLibres().shouldBe(50)
    }
  }

  describe("segundo requerimiento"){
    // Pruebas para el segundo requerimiento
    it("el vuelo a Moscú es relajado") { vueloMoscu.esRelajado().shouldBeTrue() }
    it("el vuelo a miami no es relajado") { vueloAMiami.esRelajado().shouldBeFalse() }
  }

  // Requerimientos 8, 9 y 10  (Carlos A. Martín) -->
  describe("Requerimientos 8, 9 y 10  (Carlos A. Martín)") {

    // Asignamos los vuelos a la lista de vuelos de la Empresa
    aerolineasMym.programarVuelo(barilocheMayo)
    aerolineasMym.programarVuelo(barilocheJunio)
    aerolineasMym.programarVuelo(barilocheJulio)
    aerolineasMym.programarVuelo(barilocheAgosto)
    aerolineasMym.programarVuelo(barilocheSeptiembre)
    aerolineasMym.programarVuelo(mendozaJulio)

    // Vendemos varios pasajes para tres distintos pasajeros
    aerolineasMym.venderPasaje(barilocheJulio, LocalDate.now(), 74404949)
    aerolineasMym.venderPasaje(mendozaJulio, LocalDate.now(), 74404949)
    aerolineasMym.venderPasaje(barilocheSeptiembre, LocalDate.now(), 74404949)

    aerolineasMym.venderPasaje(barilocheJunio, LocalDate.now(), 123456789)
    aerolineasMym.venderPasaje(barilocheJulio, LocalDate.now(), 123456789)
    aerolineasMym.venderPasaje(mendozaJulio, LocalDate.now(), 123456789)
    aerolineasMym.venderPasaje(barilocheSeptiembre, LocalDate.now(), 123456789)

    aerolineasMym.venderPasaje(barilocheMayo, LocalDate.now(), 1)
    aerolineasMym.venderPasaje(mendozaJulio, LocalDate.now(), 1)

    describe("octavo requerimiento"){
      it("El pasajero 74404949 tiene dos fechas para Bariloche") {
        aerolineasMym.fechasDePasajeroConDestino(74404949, "Bariloche").size.shouldBe(2)
      }
    }
    // Pruebas para requerimientos 8, 9 y 10
    describe("noveno requerimiento"){
      it("Entre 27/5/2021 y 8/7/2021 hay 52 asientos libres para el destino Mendoza") {
        aerolineasMym.asientosLibresParaDestinoEntreFechas(
          "Mendoza",
          LocalDate.of(2021, 5, 27),
          LocalDate.of(2021, 7, 8)
        ).shouldBe(52)

      }
    }

    describe("decimo requerimiento"){
      it("Los pasajeros 74404949 y 123456789 son compañeras de vuelo") {
        aerolineasMym.sonCompasDeVuelo(74404949, 123456789).shouldBeTrue()
      }
      it("Los pasajeros 74404949 y 1 no son compañeros de vuelvo") {
        aerolineasMym.sonCompasDeVuelo(74404949, 1).shouldBeFalse()
      }
    }

    // <-- Requerimientos 8, 9, 10 (Carlos A. Martín)
  }

  describe("requerimientos bonus") {

    // Asignamos los vuelos a la lista de vuelos de la Empresa
    aerolineasMym.programarVuelo(barilocheMayo)
    aerolineasMym.programarVuelo(barilocheJunio)
    aerolineasMym.programarVuelo(barilocheJulio)
    aerolineasMym.programarVuelo(barilocheAgosto)
    aerolineasMym.programarVuelo(barilocheSeptiembre)
    aerolineasMym.programarVuelo(mendozaJulio)

    // Vendemos varios pasajes para tres distintos pasajeros
    aerolineasMym.venderPasaje(barilocheJulio, LocalDate.now(), 74404949)
    aerolineasMym.venderPasaje(mendozaJulio, LocalDate.now(), 74404949)
    aerolineasMym.venderPasaje(barilocheSeptiembre, LocalDate.now(), 74404949)

    aerolineasMym.venderPasaje(barilocheJunio, LocalDate.now(), 123456789)
    aerolineasMym.venderPasaje(barilocheJulio, LocalDate.now(), 123456789)
    aerolineasMym.venderPasaje(mendozaJulio, LocalDate.now(), 123456789)
    aerolineasMym.venderPasaje(barilocheSeptiembre, LocalDate.now(), 123456789)

    aerolineasMym.venderPasaje(barilocheMayo, LocalDate.now(), 1)
    aerolineasMym.venderPasaje(mendozaJulio, LocalDate.now(), 1)

    describe("bonus 2"){
      //test bonus n°2(Tomás Miranda) -->
      it("en el dia de hoy no hay vuelos intercontinentales disponibles") {
        aerolineasMym.vuelosIntercontinentalesParaUnDia(hoy).size.shouldBe(0)
      }
      it("se programa un vuelo a moscu, otro a miami y uno a bariloche, entonces los vuelos intercontinentales son 2") {

        aerolineasMym.programarVuelo(vueloBarilocheHoy)
        aerolineasMym.programarVuelo(vueloAMiami)
        aerolineasMym.programarVuelo(vueloMoscu)

        aerolineasMym.vuelosIntercontinentalesParaUnDia(hoy).size.shouldBe(2)
      }
      // <-- test bonus n°2(Tomás Miranda)
    }

    describe("Bonus 3"){
      // Prueba Bonus 3 (Carlos A. Martín) -->
      it("El importe total vendido es distinto del efectivamente cobrado") {
        aerolineasMym.cobrarPasaje(barilocheJulio, 74404949, 3900.99)
        barilocheJulio.importeTotalDelVuelo().shouldBe(13801.98)  // aprovechamos compras previas
        barilocheJulio.importesCobradosDelVuelo().shouldBe(3900.99)
        // Realizamos un otro pago parcial
        aerolineasMym.cobrarPasaje(barilocheJulio, 74404949, 500.00)
        barilocheJulio.importesCobradosDelVuelo().shouldBe(4400.99)
      }
      // <-- Prueba Bonus 3 (Carlos A. Martín)
    }
  }
})
