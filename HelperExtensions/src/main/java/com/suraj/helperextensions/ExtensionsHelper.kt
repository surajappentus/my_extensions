package com.suraj.helperextensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.location.Address
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.text.Editable
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Tasks
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.math.roundToInt

public class ExtensionsHelper {

    fun View.show(){
        this.visibility = View.VISIBLE
    }

    fun View.hide() {
        this.visibility = View.INVISIBLE
    }

    fun View.remove(){
        this.visibility = View.GONE
    }

    fun String?.valid() : Boolean =
        this != null && !this.equals("null", true) && this.trim().isNotEmpty()

    //Email Validation
    fun String.isValidEmail(): Boolean
            = this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

//    //Phone number format
//    fun String.formatPhoneNumber(context: Context, region: String): String? {
//        val phoneNumberKit = PhoneNumberUtil.createInstance(context)
//        val number = phoneNumberKit.parse(this, region)
//        if (!phoneNumberKit.isValidNumber(number))
//            return null
//
//        return phoneNumberKit.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL)
//    }

    fun extractData(extras : Bundle){
        if (extras.containsKey("name") && extras.getString("name").valid()){
            val name = extras.getString("name")
        }
    }

    // Activity related
    inline fun <reified  T : Any> Activity.getValue(
        lable : String, defaultvalue : T? = null) = lazy{
        val value = intent?.extras?.get(lable)
        if (value is T) value else defaultvalue
    }

    inline fun <reified  T : Any> Activity.getValueNonNull(
        lable : String, defaultvalue : T? = null) = lazy{
        val value = intent?.extras?.get(lable)
        requireNotNull((if (value is T) value else defaultvalue)){lable}
    }

    // Fragment related
    inline fun <reified T: Any> Fragment.getValue(lable: String, defaultvalue: T? = null) = lazy {
        val value = arguments?.get(lable)
        if (value is T) value else defaultvalue
    }

    inline fun <reified T: Any> Fragment.getValueNonNull(lable: String, defaultvalue: T? = null) = lazy {
        val value = arguments?.get(lable)
        requireNotNull(if (value is T) value else defaultvalue) { lable }
    }
/*

    //Extensions
    fun Int.asColor() = ContextCompat.getColor(Application.instance, this)
    fun Int.asDrawable() = ContextCompat.getDrawable(Application.instance, this)
*/

    /*
    * //Usage at call site
    * val color = R.color.dark_blie.asColor()
    * val drawable = R.drawable.launcher.asDrawable()
    *
    * */



    fun Activity.toast(message: CharSequence, duration: Int = Toast.LENGTH_SHORT) = Toast.makeText(this, message, duration).show()

    inline fun Activity.alertDialog(body: AlertDialog.Builder.() -> AlertDialog.Builder): AlertDialog {
        return AlertDialog.Builder(this).body().show()
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    inline fun <reified T : Activity> Context.openActivity(vararg params: Pair<String, Any>) {
        val intent = Intent(this, T::class.java)
        intent.putExtras(*params)
        this.startActivity(intent)
    }

    fun Activity.setLoader(context: Context): Dialog {
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(false) // if you want user to wait for some process to finish,
        // here set your custom layout
//        builder.setView(R.layout.loader_dialog)
        val dialog =  builder.create()
        dialog.window?.setLayout(180, 50)
        return dialog
    }

    fun Activity.getHorizontalLinearLayoutManager (activity : Activity) : LinearLayoutManager {
        return  LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
    }


    @Throws(IOException::class)
    fun getBytes(`is`: InputStream): ByteArray? {
        val byteBuff = ByteArrayOutputStream()
        val buffSize = 1024
        val buff = ByteArray(buffSize)
        var len = 0
        while (`is`.read(buff).also { len = it } != -1) {
            byteBuff.write(buff, 0, len)
        }
        return byteBuff.toByteArray()
    }

//    fun Toast.showCustomToast(message: String, activity: Activity) {
//        val layout = activity.layoutInflater.inflate (
//            R.layout.toast_layout,
//            activity.findViewById(R.id.toast_container)
//        )
//        val textView = layout.findViewById<TextView>(R.id.toast_text)
//        textView.text = message
//        this.apply {
////        setGravity(Gravity.BOTTOM, 0, 100)
//            duration = Toast.LENGTH_SHORT
//            view = layout
//            show()
//        }
//    }

//    fun showCustomToast(message: String, activity: Activity){
//        val toast = Toast(activity)
//        val layout = activity.layoutInflater.inflate (
//            R.layout.toast_layout,
//            activity.findViewById(R.id.toast_container)
//        )
//
//        val textView = layout.findViewById<TextView>(R.id.toast_text)
//        textView.text = message
//
//        toast.apply {
//            setGravity(Gravity.BOTTOM, 0, 100)
//            duration = Toast.LENGTH_SHORT
//            view = layout
//            show()
//        }
//    }

    fun Activity.isLoaderShow(dialog: Dialog, show : Boolean){
        if(show) dialog.show()
        else dialog.dismiss()
    }

    fun Activity.setStatusBarTransparent(color: String, isDark: Boolean) {
        this.window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            if (isDark) window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.parseColor(color)
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.parseColor(color)
        }
    }

    fun Activity.transparentStatusBar(isDark: Boolean) {
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
        this.window.statusBarColor = Color.TRANSPARENT
        if (isDark) window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun setWindowFlag(activity: Activity, bits: Int, on: Boolean) {
        val win = activity.window
        val winParams = win.attributes
        if (on) winParams.flags = winParams.flags or bits
        else winParams.flags = winParams.flags and bits.inv()
        win.attributes = winParams
    }

    fun Intent.putExtras(vararg params: Pair<String, Any>): Intent {
        if (params.isEmpty()) return this
        params.forEach { (key, value) ->
            when (value) {
                is Int -> putExtra(key, value)
                is Byte -> putExtra(key, value)
                is Char -> putExtra(key, value)
                is Long -> putExtra(key, value)
                is Float -> putExtra(key, value)
                is Short -> putExtra(key, value)
                is Double -> putExtra(key, value)
                is Boolean -> putExtra(key, value)
                is Bundle -> putExtra(key, value)
                is String -> putExtra(key, value)
                is IntArray -> putExtra(key, value)
                is ByteArray -> putExtra(key, value)
                is CharArray -> putExtra(key, value)
                is LongArray -> putExtra(key, value)
                is FloatArray -> putExtra(key, value)
                is Parcelable -> putExtra(key, value)
                is ShortArray -> putExtra(key, value)
                is DoubleArray -> putExtra(key, value)
                is BooleanArray -> putExtra(key, value)
                is CharSequence -> putExtra(key, value)
                is Array<*> -> {
                    when {
                        value.isArrayOf<String>() -> putExtra(key, value as Array<String?>)
                        value.isArrayOf<Parcelable>() -> putExtra(key, value as Array<Parcelable?>)
                        value.isArrayOf<CharSequence>() -> putExtra(key, value as Array<CharSequence?>)
                        else -> putExtra(key, value)
                    }
                }
                is Serializable -> putExtra(key, value)
            }
        }
        return this
    }

    inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
        val fragmentTransaction = beginTransaction()
        fragmentTransaction.func()
        fragmentTransaction.commit()
    }

    fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int, backStackTag: String? = null) {
        supportFragmentManager.inTransaction {
            add(frameId, fragment)
            backStackTag?.let { addToBackStack(fragment.javaClass.name) }
        }
    }

    fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int, backStackTag: String? = null) {
        supportFragmentManager.inTransaction {
            replace(frameId, fragment)
            backStackTag?.let { addToBackStack(fragment.javaClass.name) }
        }
    }

    @Throws(IOException::class)
    fun getFile(context: Context, uri: Uri): File? {
        val destinationFilename =
            File(context.filesDir.path + File.separatorChar.toString() + queryName(context, uri))
        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                createFileFromStream(
                    ins!!,
                    destinationFilename
                )
            }
        } catch (ex: Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
        return destinationFilename
    }

    fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    fun Activity.BitmapToUri(bitmap : Bitmap, activity: Activity): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes)
        val path = MediaStore.Images.Media.insertImage(activity.contentResolver, bitmap, "Title", null)
        return  Uri.parse(path)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun Activity.isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager != null) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_CELLULAR")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_WIFI")
                    return true
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                    Log.i("Internet", "NetworkCapabilities.TRANSPORT_ETHERNET")
                    return true
                }
            }
        }
        return false
    }

    fun Activity.openPermissionSetting(){
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    @SuppressLint("SimpleDateFormat")
    fun getCalculatedDate(dateFormat: String?, days: Int): String? {
        val cal: Calendar = Calendar.getInstance()
        val s = SimpleDateFormat(dateFormat)
        cal.add(Calendar.DAY_OF_YEAR, days)
        return s.format(Date(cal.timeInMillis))
    }

    @SuppressLint("SimpleDateFormat")
    fun changeDateFormat(dateFormat: String?, changeDateFormat: String?, date: String ): String? {
        var spf = SimpleDateFormat(dateFormat)
        val newDate = spf.parse(date)
        spf = SimpleDateFormat(changeDateFormat)
        return  spf.format(newDate!!)
    }

    fun getLocationFromAddress(context: Context?, strAddress: String?): LatLng? {
        val coder = Geocoder(context)
        val address: List<Address>?
        var p1: LatLng? = null
        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5)
            if (address == null) {
                return null
            }
            val location: Address = address[0]
            p1 = LatLng(location.latitude, location.longitude)
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
        return p1
    }

    fun getRelationTime(time: Long): String? {
        val AVERAGE_MONTH_IN_MILLIS = DateUtils.DAY_IN_MILLIS * 30
        val now = Date().time
        val spentTime = now - (time*1000)
        val resolution: Long =
            if (spentTime <= DateUtils.MINUTE_IN_MILLIS) {
                DateUtils.SECOND_IN_MILLIS
            } else if (spentTime <= DateUtils.HOUR_IN_MILLIS) {
                DateUtils.MINUTE_IN_MILLIS
            } else if (spentTime <= DateUtils.DAY_IN_MILLIS) {
                DateUtils.HOUR_IN_MILLIS
            } else if (spentTime <= DateUtils.WEEK_IN_MILLIS) {
                DateUtils.DAY_IN_MILLIS
            } else return if (spentTime <= AVERAGE_MONTH_IN_MILLIS) {
                (spentTime / DateUtils.WEEK_IN_MILLIS).toInt().toString() + " week ago"
            } else if (spentTime <= DateUtils.YEAR_IN_MILLIS) {
                ((spentTime / AVERAGE_MONTH_IN_MILLIS).toInt()).toString() + " months ago"
            } else {
                (spentTime / DateUtils.YEAR_IN_MILLIS).toInt().toString() + " years ago"
            }
        return DateUtils.getRelativeTimeSpanString((time*1000), now, resolution).toString()
    }


    @SuppressLint("SimpleDateFormat")
    fun convertDateIntoRelationTime(oldTime: String ,format : String ) : String{
        val formatter = SimpleDateFormat(format)
        formatter.isLenient = false
        val oldDate = formatter.parse(oldTime)
        val oldMillis = (oldDate.time)/1000
        return getRelationTime(oldMillis).toString()
    }

    fun convertDateToMillisecond(oldTime: String, format: String): Long {
        val formatter = SimpleDateFormat(format)
        formatter.isLenient = false
        val oldDate = formatter.parse(oldTime)
        return (oldDate.time)
    }
    @SuppressLint("SimpleDateFormat")
    fun getTimeFromDate(dateFormat: String?, date: String ): String {
        val spf = SimpleDateFormat(dateFormat)
        val timeFormat = SimpleDateFormat("hh-mm a")
        val yearFormat = SimpleDateFormat("yyyy-MM-dd")
        val newDate = spf.parse(date)
        return when {
            yearFormat.format(newDate!!) == getCalculatedDate("yyyy-MM-dd" , 0) -> {
                "${timeFormat.format(newDate)} Today"
            }
            yearFormat.format(newDate) == getCalculatedDate("yyyy-MM-dd" , 1) -> {
                "${timeFormat.format(newDate)} Tomorrow"
            }
            else -> {
                "${timeFormat.format(newDate)}, ${changeDateFormat(dateFormat,"MMM dd", date)}"
            }
        }
    }

    fun convertMillisecondToDate(dateInMilliseconds: Long, dateFormat: String?): String {
        return DateFormat.format(dateFormat, dateInMilliseconds).toString()
    }

    /**
     * This fun is used to get auto complete fields
     * @param mGeoDataClient GeoDataClient
     * @param query String
     * @return ArrayList<AutocompletePrediction>?
     */
    fun getAutocomplete(mPlacesClient: PlacesClient, query: String): List<AutocompletePrediction> {
        val TASK_AWAIT = 120L
        var list = listOf<AutocompletePrediction>()
        val token = AutocompleteSessionToken.newInstance()

        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.ADDRESS_COMPONENTS, Place.Field.TYPES)

        val request = FindAutocompletePredictionsRequest
            .builder()
            .setTypeFilter(TypeFilter.ADDRESS)
            .setSessionToken(token)
            .setQuery(query)
            .build()

        val prediction = mPlacesClient.findAutocompletePredictions(request)

        try {
            Tasks.await(prediction, TASK_AWAIT, TimeUnit.SECONDS)
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: TimeoutException) {
            e.printStackTrace()
        }

        if (prediction.isSuccessful) {
            val findAutocompletePredictionsResponse = prediction.result
            findAutocompletePredictionsResponse?.let {
                list = findAutocompletePredictionsResponse.autocompletePredictions
            }
            return list
        }
        return list
    }

    fun fetchPlace(placesClient: PlacesClient, prediction: AutocompletePrediction): LatLng {

        var latLng :  LatLng ?= null
        // Specify the fields to return.
        val placeFields = listOf(Place.Field.ID, Place.Field.ADDRESS, Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(prediction.placeId, placeFields)

        // Use same PlacesClient instance used to fetch predictions
        placesClient.fetchPlace(request).addOnSuccessListener { response: FetchPlaceResponse ->
            response.place.latLng?.let {
                latLng = it
            }

        }.addOnFailureListener { exception: Exception ->
            if (exception is ApiException) {
                val statusCode = exception.statusCode
            }
            return@addOnFailureListener
        }
        return latLng!!
    }

//fun encode(str: String): String = Base64.encodeToString(str.toByteArray(charset("UTF-8")), Base64.DEFAULT)

    fun getQrCodeBitmap( qrCodeContent : String): Bitmap {
//    val code = Base64.encodeToString(qrCodeContent.toByteArray(charset("UTF-8")),Base64.DEFAULT)
        val code = Base64.encodeToString(qrCodeContent.toByteArray(charset("UTF-16")), Base64.DEFAULT)
        val size = 512 //pixels
        val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(code, BarcodeFormat.QR_CODE, size, size, hints)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }

    fun convertRoundDoubleNumber(value : Double): Double {
        return (value * 100.0).roundToInt() / 100.0
    }


    @Throws(IOException::class)
    fun createImageFileFromBitmap(image: Bitmap, context: Context): File? {

        val photo: File
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val directory: File = context.filesDir

        // Save Bitmap
        photo = File(directory, "$imageFileName.jpg")
        photo.createNewFile()
        // Write to/Compress the Bitmap from the camera intent to the file
        val fos = FileOutputStream(photo)
        image.compress(Bitmap.CompressFormat.PNG, 90, fos)
        fos.close()

        // path to file
        val imgPath = photo.absolutePath
        return photo

    }


    fun Context.getColorCompat(@ColorRes resourceId: Int) = ContextCompat.getColor(this, resourceId)

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun setTextViewDrawableColor(textView: TextView, color: Int) {
        for (drawable in textView.compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(ContextCompat.getColor(textView.context, color), PorterDuff.Mode.SRC_IN)
            }
        }
    }

}