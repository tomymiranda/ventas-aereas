package ar.edu.unahur.obj2.ventasAereas

import java.time.LocalDate


class Empresa(var criterioDeVenta: Criterio, var politicaDeVenta: Politica) {

    var vuelosProgramados = mutableListOf<Vuelo>()
    var pesoQuePermiteLlevarPorPasajero = 10.0

    fun programarVuelo(nuevoVuelo: Vuelo) = vuelosProgramados.add(nuevoVuelo)

    fun cambiarPolitica(nuevaPoliticaDeVenta: Politica) { politicaDeVenta = nuevaPoliticaDeVenta }
    fun cambiarCriterio(nuevoCriterioDeVenta: Criterio) { criterioDeVenta = nuevoCriterioDeVenta }
    fun puedeVenderPorCriterio(vueloEnDondeSeBuscaVender: Vuelo) = criterioDeVenta.puedeVender(vueloEnDondeSeBuscaVender)

    fun venderPasaje(vueloDondeSeVende: Vuelo, fechaDeVenta: LocalDate, dniDeComprador: Int){
        check(criterioDeVenta.puedeVender(vueloDondeSeVende)) {
            "No hay pasajes disponibles para este vuelo"
        }
        val pasajeVendido = Pasaje(fechaDeVenta,dniDeComprador,this.precioDeVenta(vueloDondeSeVende))
        vueloDondeSeVende.agregarPasajero(pasajeVendido)
    }

    fun precioDeVenta(vueloEnDondeSeBuscaVender: Vuelo) = politicaDeVenta.precioConElQueSeVende(vueloEnDondeSeBuscaVender)


    fun importeTotalGeneradoParaUnVuelo(vueloEnDondeSeVendio: Vuelo) = vueloEnDondeSeVendio.importeTotalDelVuelo()


    // Requerimientos 8, 9 y 10  (Carlos A. Martín) -->
    fun vuelosAlDestino(destinoBuscado: String) = vuelosProgramados.filter { it.destino.nombre == destinoBuscado }

    fun fechasDePasajeroConDestino(documentoPasajero: Int, destinoBuscado: String) : List<LocalDate> {
        val vuelosDelPasajeroAlDestino = vuelosAlDestino(destinoBuscado).filter { it.pasajeroPerteneceAlVuelo(documentoPasajero) }
        return vuelosDelPasajeroAlDestino.map { it.fecha }
    }

    fun asientosLibresParaDestinoEntreFechas(destinoBuscado: String, fechaInicial: LocalDate, fechaFinal: LocalDate) : Int {
        val vuelosEntreFechas = vuelosAlDestino(destinoBuscado).filter { it.fecha.isAfter(fechaInicial) && it.fecha.isBefore(fechaFinal) }
        return vuelosEntreFechas.sumBy { it.cantidadDeAsientosLibres() }
    }

    fun sonCompasDeVuelo(documentoPrimerPasajero: Int, documentoSegundoPasajero: Int) : Boolean {
        val vuelosPrimerPasajero = vuelosProgramados.filter { it.pasajeroPerteneceAlVuelo(documentoPrimerPasajero) }
        return vuelosPrimerPasajero.filter { it.pasajeroPerteneceAlVuelo(documentoSegundoPasajero)}.size >= 3
    }
    // <-- Requerimientos 8, 9, 10 (Carlos A. Martín)

    //bonus n°2
    fun vuelosIntercontinentalesParaUnDia(dia:LocalDate) = this.vuelosIntercontinentales().filter { it.fecha == dia }

    fun vuelosIntercontinentales() = vuelosProgramados.filter {it.esUnVueloIntercontinental()}

    // Bonus 3 (Carlos A. Martín) -->
    fun cobrarPasaje(vueloDelPasajero: Vuelo, documentoPasajero: Int, importeAbonado: Double) {
        val pasajeDelPasajero = vueloDelPasajero.pasajesVendidosDelVuelo.filter { it.dniUsuario == documentoPasajero }.get(0)
        pasajeDelPasajero.registrarPago(importeAbonado)
    }

    fun cuantoDebe(documentoPasajero: Int) : Double {
        val vuelosDelPasajero = vuelosProgramados.filter { it.pasajeroPerteneceAlVuelo(documentoPasajero) }
        val pasajesDelPasajero = vuelosDelPasajero.map { it.pasajesVendidosDelVuelo.filter { it.dniUsuario == documentoPasajero }}.flatten()
        return pasajesDelPasajero.sumByDouble { it.importePendiente() }
    }
    // <-- Bonus 3 (Carlos A. Martín)
}

abstract class Politica {
    abstract fun precioConElQueSeVende(vueloQueSeBuscaVender:Vuelo) : Double
}

object Estricta: Politica() {
    override fun precioConElQueSeVende(vueloQueSeBuscaVender: Vuelo) : Double {
        return vueloQueSeBuscaVender.precioEstandar
    }
}

object VentaAnticipada: Politica() {
    override fun precioConElQueSeVende(vueloQueSeBuscaVender: Vuelo) : Double {
        val precioCalculado : Double
        when (vueloQueSeBuscaVender.cantidadDeAsientosOcupados()) {
            in 0..39 -> precioCalculado = vueloQueSeBuscaVender.precioEstandar * 0.3
            in 40..79 -> precioCalculado = vueloQueSeBuscaVender.precioEstandar * 0.6
            else -> precioCalculado = vueloQueSeBuscaVender.precioEstandar
        }
        return  precioCalculado
    }
}

object Remate: Politica() {
    override fun precioConElQueSeVende(vueloQueSeBuscaVender: Vuelo) : Double {
        return if (vueloQueSeBuscaVender.cantidadDeAsientosLibres() > 30) vueloQueSeBuscaVender.precioEstandar * 0.25 else vueloQueSeBuscaVender.precioEstandar * 0.5
    }
}

abstract class Criterio {
    abstract fun puedeVender(vueloEnElCualSeBuscaVender: Vuelo) : Boolean
}
object Segura: Criterio() {
    override fun puedeVender(vueloEnElCualSeBuscaVender: Vuelo): Boolean {
        return vueloEnElCualSeBuscaVender.cantidadDeAsientosLibres() >= 3
    }
}

object LaxaFija: Criterio() {
    override fun puedeVender(vueloEnElCualSeBuscaVender: Vuelo): Boolean {
        return (vueloEnElCualSeBuscaVender.cantidadDeAsientosDisponibles() + 10) > vueloEnElCualSeBuscaVender.cantidadDeAsientosOcupados()
    }
}

object LaxaPorcentual: Criterio(){
    override fun puedeVender(vueloEnElCualSeBuscaVender: Vuelo): Boolean {
        return (vueloEnElCualSeBuscaVender.cantidadDeAsientosDisponibles() * 1.10) > vueloEnElCualSeBuscaVender.cantidadDeAsientosOcupados()
    }
}

object Pandemia: Criterio() {
    override fun puedeVender(vueloEnElCualSeBuscaVender: Vuelo): Boolean = false
}

class Pasaje(val fechaDeVenta: LocalDate, val dniUsuario: Int, val precio: Double ) {
    // Bonus 3 (Carlos A. Martín) -->
    var importeAbonado : Double = 0.0

    fun importePendiente() = precio - importeAbonado
    fun registrarPago(importe: Double) { importeAbonado += importe }
    // <-- Bonus 3 (Carlos A. Martín)
}