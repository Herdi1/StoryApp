package com.abid.storyapp

import com.abid.storyapp.data.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem>{
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100){
            val story = ListStoryItem(
                i.toString(),
                "date:$i",
                "user$i",
                "thisIsDescription$i",
                0.00,
                "story-$i",
                0.00
            )
            items.add(story)
        }
        return items
    }
}