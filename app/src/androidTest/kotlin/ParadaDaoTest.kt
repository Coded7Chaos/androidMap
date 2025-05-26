package com.tonygnk.maplibredemo
import android.app.Application
import androidx.test.core.app.ApplicationProvider
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tonygnk.maplibredemo.data.ParadaDao
import com.tonygnk.maplibredemo.data.TransporteDatabase
import org.junit.Before
import androidx.compose.ui.platform.LocalContext
import androidx.room.Room
import android.content.Context
import com.tonygnk.maplibredemo.models.Parada
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Test
import java.io.IOException
import kotlin.jvm.Throws
import org.junit.Assert.assertEquals
/*
@RunWith(AndroidJUnit4::class)
class ParadaDaoTest {
    private lateinit var paradaDao: ParadaDao
    private lateinit var transporteDatabase: TransporteDatabase



    private var parada1 = Parada(
        id_parada = 1,
        lat = -18.131564,
        lon = -68.16464,
        nombre = "Obrajes calle 4",
        direccion = "Av Hector Ormache",
        estado = true
    )
    private var parada2 = Parada(2, -65.1684, -464.16456, "Isabel la catolica", "Zona San pedro",true)

    private suspend fun addParada(){
        paradaDao.insert(parada1)
    }
    private suspend fun addParadas(){
        paradaDao.insert(parada1)
        paradaDao.insert(parada2)
    }

    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsParadasIntoDB() = runBlocking {
        addParada()
        val allParadas = paradaDao.getAllItems().first()
        assertEquals(allParadas[0], parada1)
    }

    @Test
    @Throws(Exception::class)
    fun daoGetAllItems_returnsAllParadasFromDB() = runBlocking {
        addParadas()
        val allParadas = paradaDao.getAllItems().first()
        assertEquals(allParadas[0], parada1)
        assertEquals(allParadas[2], parada2)
    }
    @Before
    fun createDb(){
        val context: Context = ApplicationProvider.getApplicationContext()
        transporteDatabase = Room.inMemoryDatabaseBuilder(context, TransporteDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        paradaDao = transporteDatabase.paradaDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb(){
        transporteDatabase.close()
    }
}
*/