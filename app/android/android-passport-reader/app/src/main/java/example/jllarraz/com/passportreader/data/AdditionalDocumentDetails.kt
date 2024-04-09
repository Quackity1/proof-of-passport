import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

import java.util.ArrayList

class AdditionalDocumentDetails : Parcelable {

    var endorsementsAndObservations: String? = null
    var dateAndTimeOfPersonalization: String? = null
    var dateOfIssue: String? = null
    var imageOfFront: Bitmap? = null
    var imageOfRear: Bitmap? = null
    var issuingAuthority: String? = null
    var namesOfOtherPersons: List<String>? = null
    var personalizationSystemSerialNumber: String? = null
    var taxOrExitRequirements: String? = null
    var tag: Int = 0
    var tagPresenceList: List<Int>? = null

    constructor() {
        namesOfOtherPersons = ArrayList()
        tagPresenceList = ArrayList()
    }

    constructor(`in`: Parcel) {
        this.readFromParcel(`in`)
    }

    private fun readFromParcel(`in`: Parcel) {
        endorsementsAndObservations = `in`.readNullableString()
        dateAndTimeOfPersonalization = `in`.readNullableString()
        dateOfIssue = `in`.readNullableString()
        imageOfFront = `in`.readParcelable(Bitmap::class.java.classLoader)
        imageOfRear = `in`.readParcelable(Bitmap::class.java.classLoader)
        issuingAuthority = `in`.readNullableString()
        namesOfOtherPersons = `in`.readNullableList()
        personalizationSystemSerialNumber = `in`.readNullableString()
        taxOrExitRequirements = `in`.readNullableString()
        tag = `in`.readInt()
        tagPresenceList = `in`.readNullableList()
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeNullableString(endorsementsAndObservations)
        dest.writeNullableString(dateAndTimeOfPersonalization)
        dest.writeNullableString(dateOfIssue)
        dest.writeParcelable(imageOfFront, flags)
        dest.writeParcelable(imageOfRear, flags)
        dest.writeNullableString(issuingAuthority)
        dest.writeNullableList(namesOfOtherPersons)
        dest.writeNullableString(personalizationSystemSerialNumber)
        dest.writeNullableString(taxOrExitRequirements)
        dest.writeInt(tag)
        dest.writeNullableList(tagPresenceList)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AdditionalDocumentDetails> = object : Parcelable.Creator<AdditionalDocumentDetails> {
            override fun createFromParcel(pc: Parcel): AdditionalDocumentDetails {
                return AdditionalDocumentDetails(pc)
            }

            override fun newArray(size: Int): Array<AdditionalDocumentDetails?> {
                return arrayOfNulls(size)
            }
        }
    }
}

// Extension functions to simplify read and write operations
private fun Parcel.readNullableString(): String? = if (readInt() == 1) readString() else null

private fun Parcel.writeNullableString(value: String?) {
    writeInt(if (value != null) 1 else 0)
    if (value != null) writeString(value)
}

private fun <T> Parcel.readNullableList(): List<T>? {
    val size = readInt()
    return if (size >= 0) {
        val list = ArrayList<T>(size)
        for (i in 0 until size) {
            @Suppress("UNCHECKED_CAST")
            list.add(readValue(null) as T)
        }
        list
    } else {
        null
    }
}

private fun <T> Parcel.writeNullableList(list: List<T>?) {
    writeInt(list?.size ?: -1)
    list?.forEach { writeValue(it) }
}
