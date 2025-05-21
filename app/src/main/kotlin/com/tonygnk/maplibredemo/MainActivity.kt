package com.tonygnk.maplibredemo

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import org.ramani.compose.CameraPosition
import org.ramani.compose.MapApplier
import org.ramani.compose.MapLibre
import org.ramani.compose.Polyline

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyMap()
        }
    }
}

@Composable
fun MyMap(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val styleBuilder = remember {
        val styleManager = MapStyleManager(context)
        val style = when (val result = styleManager.setupStyle()) {
            is MapStyleManager.StyleSetupResult.Error -> {
                throw result.exception
            }

            is MapStyleManager.StyleSetupResult.Success -> result.styleFile
        }
        Style.Builder().fromUri(
            Uri.fromFile(style).toString()
        )
    }

    val cameraPosition = rememberSaveable {
       CameraPosition(
            target = LatLng(-16.5, -68.15),
            zoom = 18.0,
        )
    }

    MapLibre(
        modifier = modifier,
        styleBuilder = styleBuilder,
        cameraPosition = cameraPosition
    ) {

        // Add map markers, polylines, etc.
        /*
        val points = mutableListOf<Point>()
        points.add(Point.fromLngLat(-16.5000, -68.1500))
        points.add(Point.fromLngLat(-16.5100, -68.1600))
        points.add(Point.fromLngLat(-16.5200, -68.1700))
        */

        val rutaAchumani = listOf(
            LatLng( -16.503101799307196, -68.13236832618715 ),
            LatLng( -16.506513833115456, -68.12868297100069 ),
            LatLng( -16.508148425451957, -68.13022792339326 ),
            LatLng( -16.51261816277617, -68.12542140483858 ),
            LatLng( -16.513255532498974, -68.12487959861757 ),
            LatLng( -16.513908320278166, -68.12491714954378 ),
            LatLng( -16.514139622505486, -68.12509417533876 ),
            LatLng( -16.514262983580185, -68.12529265880586 ),
            LatLng( -16.514473725233966, -68.1253892183304 ),
            LatLng( -16.514653626463925, -68.1253355741501 ),
            LatLng( -16.514741007000907, -68.12508881092073 ),
            LatLng( -16.514612506197537, -68.12482595443727 ),
            LatLng( -16.514288683794167, -68.12462210655214 ),
            LatLng( -16.514124202365604, -68.12437534332277 ),
            LatLng( -16.514052241696593, -68.12419295310976 ),
            LatLng( -16.514083081986573, -68.12381744384767 ),
            LatLng( -16.513944300642798, -68.1232112646103 ),
            LatLng( -16.514124202365604, -68.12301814556123 ),
            LatLng( -16.515100808795673, -68.1230664253235 ),
            LatLng( -16.51536294968141, -68.12293767929079 ),
            LatLng( -16.515440049874265, -68.12258899211885 ),
            LatLng( -16.515378369722445, -68.12203645706178 ),
            LatLng( -16.516354969814085, -68.12067389488222 ),
            LatLng( -16.516832988059686, -68.12071681022645 ),
            LatLng( -16.517146526266732, -68.12111914157869 ),
            LatLng( -16.517346984853788, -68.12120497226717 ),
            LatLng( -16.517552583188547, -68.12116742134096 ),
            LatLng( -16.517727341601073, -68.12093138694765 ),
            LatLng( -16.517691361940834, -68.12062025070192 ),
            LatLng( -16.517290445273343, -68.12030911445619 ),
            LatLng( -16.516838128034394, -68.12019109725954 ),
            LatLng( -16.516416649654175, -68.12026083469392 ),
            LatLng( -16.516051710314013, -68.12045931816102 ),
            LatLng( -16.515804990369602, -68.120453953743 ),
            LatLng( -16.51572275031815, -68.120094537735 ),
            LatLng( -16.516051710314013, -68.11986386775972 ),
            LatLng( -16.517100266563254, -68.11971902847291 ),
            LatLng( -16.517573143009965, -68.11938643455507 ),
            LatLng( -16.51812311742156, -68.11861932277681 ),
            LatLng( -16.518354414603643, -68.11798095703126 ),
            LatLng( -16.518714663806385, -68.11783611774446 ),
            LatLng( -16.519254355148032, -68.11809897422792 ),
            LatLng( -16.519254355148032, -68.11842620372774 ),
            LatLng( -16.51861186528468, -68.1187427043915 ),
            LatLng( -16.518416547942703, -68.11909675598146 ),
            LatLng( -16.51859130557378, -68.1194454431534 ),
            LatLng( -16.518976799789215, -68.11934888362886 ),
            LatLng( -16.519583309130994, -68.11865150928499 ),
            LatLng( -16.520960797850723, -68.1177717447281 ),
            LatLng( -16.521145832900544, -68.11738014221193 ),
            LatLng( -16.521135553180212, -68.11627507209779 ),
            LatLng( -16.522369964687215, -68.1149983406067 ),
            LatLng( -16.524261411930844, -68.11321735382081 ),
            LatLng( -16.52468801296103, -68.11243414878847 ),
            LatLng( -16.527442907763678, -68.10723066329957 ),
            LatLng( -16.526872401059357, -68.10690879821779 ),
            LatLng( -16.53069023718023, -68.09994578361513 ),
            LatLng( -16.53139436191792, -68.0991840362549 ),
            LatLng( -16.53519761901533, -68.09631407260896 ),
            LatLng( -16.541178085547152, -68.09386253356935 ),
            LatLng( -16.541578952612458, -68.09348702430727 ),
            LatLng( -16.541733132031144, -68.09312224388124 ),
            LatLng( -16.54072325321239, -68.08939933776857 ),
            LatLng( -16.541408648530606, -68.08877706527711 ),
            LatLng( -16.538908872869822, -68.07976484298707 ),
            LatLng( -16.539124727008808, -68.0756986141205 ),
            LatLng( -16.53465341339756, -68.07538747787477 ),
            LatLng( -16.531932879028787, -68.07364404201509 ),
            LatLng( -16.532534208123103, -68.07273745536806 ),
            LatLng( -16.532230974027637, -68.07249605655672 ),
            LatLng( -16.530498531544374, -68.06970655918123 ),
            LatLng( -16.528361876976604, -68.071106672287 ),
            LatLng( -16.526593532907423, -68.06841373443605 ),
            LatLng( -16.522871670353876, -68.06639671325685 ),
            LatLng( -16.522614679477478, -68.06607484817506 ),
            LatLng( -16.521812865745385, -68.06407928466798 ),
            LatLng( -16.5214941952609, -68.06372523307802 ),
            LatLng( -16.5211652445319, -68.06377887725831 ),
            LatLng( -16.519355770880743, -68.0624806880951 ),
            LatLng( -16.518111909222345, -68.06233048439027 ),
            LatLng( -16.517042798587447, -68.06167602539064 ),
            LatLng( -16.515901438027278, -68.06120395660402 ),
            LatLng( -16.513382889751757, -68.05829644203187 ),
            LatLng( -16.511378253402203, -68.05515289306642 ),
            LatLng( -16.50991845384954, -68.05122613906862 ),
            LatLng( -16.509939014483155, -68.05093646049501 ),
            LatLng( -16.50933823775577, -68.0512046813965 ),
            LatLng( -16.509081228879506, -68.05119395256044 ),
            LatLng( -16.508891042091136, -68.0516016483307 ),
            LatLng( -16.5087779579661, -68.05227756500246 ),
            LatLng( -16.508613471847962, -68.0523204803467 ),
            LatLng( -16.507394989011203, -68.05141389369966 ),
            LatLng( -16.506901526707008, -68.05198788642885 ),
            LatLng( -16.507466952158694, -68.05265843868257 )
        )

        Polyline(rutaAchumani, color = "Red", lineWidth = 5.0f)
        /*
        val lineString = remember(points) {
            LineString.fromLngLats(points)
        }

        val source =  remember{
            GeoJsonSource("polyline-source", LineString.fromLngLats(points))
        }

        val layer = remember {
            LineLayer("polyline-layer", "polyline-source").apply {
                withProperties(
                    PropertyFactory.lineColor("#FF0000"),
                    PropertyFactory.lineWidth(5f)
                )
            }
        }
        */




        //fin de codigo a;adido

    }
}
