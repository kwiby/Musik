package com.kwiby.musik.data.data_classes

data class Metadata(
	val filePath: String?,
	val durationMs: String?,
	val bitRate: String?,
	val sampleRate: String?,
	val mimeType: String?,
	val artwork: ByteArray?,
	val title: String?,
	val artist: String?,
	val album: String?,
	val albumArtist: String?,
	val trackNumber: String?,
	val discNumber: String?,
	val genre: String?,
	val year: String?
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Metadata

		if (filePath != other.filePath) return false
		if (durationMs != other.durationMs) return false
		if (bitRate != other.bitRate) return false
		if (sampleRate != other.sampleRate) return false
		if (mimeType != other.mimeType) return false
		if (artwork != null) {
			if (other.artwork == null) return false
			if (!artwork.contentEquals(other.artwork)) return false
		} else if (other.artwork != null) return false
		if (title != other.title) return false
		if (artist != other.artist) return false
		if (album != other.album) return false
		if (albumArtist != other.albumArtist) return false
		if (trackNumber != other.trackNumber) return false
		if (discNumber != other.discNumber) return false
		if (genre != other.genre) return false
		if (year != other.year) return false

		return true
	}

	override fun hashCode(): Int {
		var result = filePath?.hashCode() ?: 0
		result = 31 * result + (durationMs?.hashCode() ?: 0)
		result = 31 * result + (bitRate?.hashCode() ?: 0)
		result = 31 * result + (sampleRate?.hashCode() ?: 0)
		result = 31 * result + (mimeType?.hashCode() ?: 0)
		result = 31 * result + (artwork?.contentHashCode() ?: 0)
		result = 31 * result + (title?.hashCode() ?: 0)
		result = 31 * result + (artist?.hashCode() ?: 0)
		result = 31 * result + (album?.hashCode() ?: 0)
		result = 31 * result + (albumArtist?.hashCode() ?: 0)
		result = 31 * result + (trackNumber?.hashCode() ?: 0)
		result = 31 * result + (discNumber?.hashCode() ?: 0)
		result = 31 * result + (genre?.hashCode() ?: 0)
		result = 31 * result + (year?.hashCode() ?: 0)
		return result
	}

}