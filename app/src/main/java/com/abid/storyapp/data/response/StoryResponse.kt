package com.abid.storyapp.data.response

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "story_response")
data class StoryResponse(

	@field:SerializedName("listStory")
	val listStory: List<ListStoryItem?>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
)


//data class ListStoryItem(
//
//)
