package ar.edu.unahur.obj2.ventasAereas

import java.time.LocalDate


abstract class Vuelo(val fecha: LocalDate, val avion: Avion, val origen: Ciudad, val destino: Ciudad,  val precioEstandar: Double) {

    val pasajesVendidosDelVuelo = mutableListOf<Pasaje>()
    val asociacion = IATA

    abstract fun cantidadDeAsientosDisponibles(): Int
    fun cantidadDeAsientosOcupados() = pasajesVendidosDelVuelo.size
    fun cantidadDeAsientosLibres() = this.cantidadDeAsientosDisponibles() - this.cantidadDeAsientosOcupados()

    fun agregarPasajero(pasajeVendido: Pasaje) { pasajesVendidosDelVuelo.add(pasajeVendido) }
    fun pasajeroPerteneceAlVuelo(documentoDelPasajero: Int) = pasajesVendidosDelVuelo.any() { it.dniUsuario == documentoDelPasajero }

    fun esRelajado(): Boolean { return avion.alturaDeLaCabina > 4.0 && cantidadDeAsientosDisponibles() <= 100 }


    // Requerimientos 6 y 7 (Tomás Miranda) --->>
    // Requerimiento 6
    fun importeTotalDelVuelo() =  pasajesVendidosDelVuelo.sumByDouble { it.precio }

    // Requerimiento 7
    fun pesoDelVuelo(EmpresaQueAdministraElVuelo:Empresa) = avion.pesoDelAvion + this.pesoPorLaCantidadDePasajerosEnElVuelo() + this.pesoDeCarga(EmpresaQueAdministraElVuelo)

    fun pesoPorLaCantidadDePasajerosEnElVuelo() = this.cantidadDeAsientosOcupados() * asociacion.pesoPorPasajero

    abstract fun pesoDeCarga(EmpresaDelVuelo: Empresa): Double
    // <<---- Requerimiento 6 y 7

    // Bonus 2 (Tomás Miranda)
    fun esUnVueloIntercontinental() = origen.continente != destino.continente

    // Bonus 3 (Carlos A. Martín) -->
    fun importesCobradosDelVuelo() = pasajesVendidosDelVuelo.sumByDouble { it.importeAbonado }
    // <-- Bonus 3 (Carlos A. Martín)

}

class VueloPasajero(fecha: LocalDate, avion: Avion, origen: Ciudad, destino: Ciudad, precioEstandar: Double) : Vuelo(fecha, avion, origen, destino,  precioEstandar) {
    override fun cantidadDeAsientosDisponibles() = avion.cantidadDeAsientos
    override fun pesoDeCarga(EmpresaDelVuelo: Empresa) = this.cantidadDeAsientosOcupados() * EmpresaDelVuelo.pesoQuePermiteLlevarPorPasajero
}

class VueloCarga (fecha: LocalDate, avion: Avion, origen: Ciudad, destino: Ciudad, precioEstandar: Double, val pesoDeCarga: Double) : Vuelo(fecha, avion, origen, destino, precioEstandar){
    override fun cantidadDeAsientosDisponibles() = 10
    override fun pesoDeCarga(EmpresaDelVuelo: Empresa) = pesoDeCarga + 700.0
}

class VueloCharter (fecha: LocalDate, avion: Avion, origen: Ciudad, destino: Ciudad,precioEstandar: Double) : Vuelo(fecha, avion, origen, destino,precioEstandar){
    override fun cantidadDeAsientosDisponibles() = avion.cantidadDeAsientos - 25
    override fun pesoDeCarga(EmpresaDelVuelo: Empresa) = 5000.0
}

class Avion(val cantidadDeAsientos: Int, val alturaDeLaCabina: Double,val pesoDelAvion: Double) { }


object  IATA {
    var pesoPorPasajero = 20.0
}

class Ciudad(val nombre: String,val continente: Continente){}

abstract class Continente{}
object AmericaDelSur: Continente(){}
object AmericaCentral: Continente(){}
object AmericaDelNorte: Continente(){}
object Europa: Continente(){}
object Asia: Continente(){}