package example.jllarraz.com.passportreader.data

import android.os.Parcel
import android.os.Parcelable

class AdditionalPersonDetails : Parcelable {

    var custodyInformation: String? = null
    var fullDateOfBirth: String? = null
    var nameOfHolder: String? = null
    var otherNames: List<String>? = null
    var otherValidTDNumbers: List<String>? = null
    var permanentAddress: List<String>? = null
    var personalNumber: String? = null
    var personalSummary: String? = null
    var placeOfBirth: List<String>? = null
    var profession: String? = null
    var proofOfCitizenship: ByteArray? = null
    var tag: Int = 0
    var tagPresenceList: List<Int>? = null
    var telephone: String? = null
    var title: String? = null

    constructor() {
        otherNames = ArrayList()
        otherValidTDNumbers = ArrayList()
        permanentAddress = ArrayList()
        placeOfBirth = ArrayList()
        tagPresenceList = ArrayList()
    }

    constructor(`in`: Parcel) {
        otherNames = ArrayList()
        otherValidTDNumbers = ArrayList()
        permanentAddress = ArrayList()
        placeOfBirth = ArrayList()
        tagPresenceList = ArrayList()

        this.custodyInformation = `in`.readNullableString()
        this.fullDateOfBirth = `in`.readNullableString()
        this.nameOfHolder = `in`.readNullableString()
        this.otherNames = `in`.readNullableList()
        this.otherValidTDNumbers = `in`.readNullableList()
        this.permanentAddress = `in`.readNullableList()
        this.personalNumber = `in`.readNullableString()
        this.personalSummary = `in`.readNullableString()
        this.placeOfBirth = `in`.readNullableList()
        this.profession = `in`.readNullableString()
        this.proofOfCitizenship = `in`.readNullableByteArray()
        this.tag = `in`.readInt()
        this.tagPresenceList = `in`.readNullableList()
        this.telephone = `in`.readNullableString()
        this.title = `in`.readNullableString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        writeNullableString(dest, custodyInformation)
        writeNullableString(dest, fullDateOfBirth)
        writeNullableString(dest, nameOfHolder)
        writeNullableList(dest, otherNames)
        writeNullableList(dest, otherValidTDNumbers)
        writeNullableList(dest, permanentAddress)
        writeNullableString(dest, personalNumber)
        writeNullableString(dest, personalSummary)
        writeNullableList(dest, placeOfBirth)
        writeNullableString(dest, profession)
        writeNullableByteArray(dest, proofOfCitizenship)
        dest.writeInt(tag)
        writeNullableList(dest, tagPresenceList)
        writeNullableString(dest, telephone)
        writeNullableString(dest, title)
    }

    private fun writeNullableString(dest: Parcel, value: String?) {
        dest.writeInt(if (value != null) 1 else 0)
        if (value != null) {
            dest.writeString(value)
        }
    }

    private fun writeNullableList(dest: Parcel, list: List<String>?) {
        dest.writeInt(if (list != null) 1 else 0)
        if (list != null) {
            dest.writeList(list)
        }
    }

    private fun writeNullableByteArray(dest: Parcel, byteArray: ByteArray?) {
        dest.writeInt(if (byteArray != null) 1 else 0)
        if (byteArray != null) {
            dest.writeInt(byteArray.size)
            dest.writeByteArray(byteArray)
        }
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<AdditionalPersonDetails> = object : Parcelable.Creator<AdditionalPersonDetails> {
            override fun createFromParcel(pc: Parcel): AdditionalPersonDetails {
                return AdditionalPersonDetails(pc)
            }

            override fun newArray(size: Int): Array<AdditionalPersonDetails?> {
                return arrayOfNulls(size)
            }
        }
    }
}
