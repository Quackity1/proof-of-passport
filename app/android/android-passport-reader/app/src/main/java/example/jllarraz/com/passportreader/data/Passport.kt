import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable
import org.jmrtd.FeatureStatus
import org.jmrtd.VerificationStatus
import org.jmrtd.lds.SODFile
import java.util.ArrayList

class Passport : Parcelable {

    var sodFile: SODFile? = null
    var face: Bitmap? = null
    var portrait: Bitmap? = null
    var signature: Bitmap? = null
    var fingerprints: List<Bitmap>? = null
    var personDetails: PersonDetails? = null
    var additionalPersonDetails: AdditionalPersonDetails? = null
    var additionalDocumentDetails: AdditionalDocumentDetails? = null
    var featureStatus: FeatureStatus? = null
    var verificationStatus: VerificationStatus? = null

    constructor(`in`: Parcel) {
        fingerprints = ArrayList()

        face = readBitmap(`in`)
        portrait = readBitmap(`in`)
        personDetails = readParcelable(`in`, PersonDetails.CREATOR)
        additionalPersonDetails = readParcelable(`in`, AdditionalPersonDetails.CREATOR)
        readList(`in`, fingerprints!!, Bitmap::class.java.classLoader)
        signature = readBitmap(`in`)
        additionalDocumentDetails = readParcelable(`in`, AdditionalDocumentDetails.CREATOR)
        sodFile = readSODFile(`in`)
        featureStatus = readParcelable(`in`, FeatureStatus.CREATOR)
        verificationStatus = readParcelable(`in`, VerificationStatus.CREATOR)
    }

    constructor() {
        fingerprints = ArrayList()
        featureStatus = FeatureStatus()
        verificationStatus = VerificationStatus()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        writeBitmap(dest, face)
        writeBitmap(dest, portrait)
        writeParcelable(dest, personDetails, flags)
        writeParcelable(dest, additionalPersonDetails, flags)
        dest.writeList(fingerprints)
        writeBitmap(dest, signature)
        writeParcelable(dest, additionalDocumentDetails, flags)
        writeSODFile(dest, sodFile)
        writeParcelable(dest, featureStatus, flags)
        writeParcelable(dest, verificationStatus, flags)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Passport> = object : Parcelable.Creator<Passport> {
            override fun createFromParcel(source: Parcel): Passport {
                return Passport(source)
            }

            override fun newArray(size: Int): Array<Passport?> {
                return arrayOfNulls(size)
            }
        }

        private fun readBitmap(parcel: Parcel): Bitmap? {
            return if (parcel.readInt() == 1) parcel.readParcelable(Bitmap::class.java.classLoader) else null
        }

        private fun writeBitmap(parcel: Parcel, bitmap: Bitmap?) {
            parcel.writeInt(if (bitmap != null) 1 else 0)
            bitmap?.let { parcel.writeParcelable(it, 0) }
        }

        private fun <T : Parcelable> readParcelable(parcel: Parcel, creator: Parcelable.Creator<T>): T? {
            return if (parcel.readInt() == 1) creator.createFromParcel(parcel) else null
        }

        private fun <T : Parcelable> writeParcelable(parcel: Parcel, item: T?, flags: Int) {
            parcel.writeInt(if (item != null) 1 else 0)
            item?.writeToParcel(parcel, flags)
        }

        private fun readSODFile(parcel: Parcel): SODFile? {
            return if (parcel.readInt() == 1) parcel.readSerializable() as SODFile else null
        }

        private fun writeSODFile(parcel: Parcel, sodFile: SODFile?) {
            parcel.writeInt(if (sodFile != null) 1 else 0)
            sodFile?.let { parcel.writeSerializable(it) }
        }
    }
}
