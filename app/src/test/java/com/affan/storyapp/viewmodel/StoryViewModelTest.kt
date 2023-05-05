package com.affan.storyapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.affan.storyapp.DataDummy
import com.affan.storyapp.MainDispatcherRule
import com.affan.storyapp.adapter.RecyclerAdapter
import com.affan.storyapp.data.StoryRepository
import com.affan.storyapp.entity.ListStoryItem
import com.affan.storyapp.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest{
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()
    @Mock
    private lateinit var storyRepository: StoryRepository

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyData = DataDummy.generateStoryResponse()
        val data: PagingData<ListStoryItem> = StoryPagingSource.snapshot(dummyData)
        val expected = MutableLiveData<PagingData<ListStoryItem>>()
        expected.value = data
        Mockito.`when`(storyRepository.getStory()).thenReturn(expected)

        val storyViewModel = StoryViewModel(storyRepository)
        val actual: PagingData<ListStoryItem> = storyViewModel.getStory().getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = RecyclerAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actual)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyData.size, differ.snapshot().size)
        Assert.assertEquals(dummyData[0], differ.snapshot()[0])
    }
    @Test
    fun `when Get Stories Empty Should Return No Data`() = runTest {
        val data: PagingData<ListStoryItem> = PagingData.from(emptyList())
        val expected = MutableLiveData<PagingData<ListStoryItem>>()
        expected.value = data
        Mockito.`when`(storyRepository.getStory()).thenReturn(expected)
        val storyviewmodel = StoryViewModel(storyRepository)
        val actual: PagingData<ListStoryItem> = storyviewmodel.getStory().getOrAwaitValue()
        val differ = AsyncPagingDataDiffer(
            diffCallback = RecyclerAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actual)
        Assert.assertEquals(0, differ.snapshot().size)
    }

}
class StoryPagingSource : PagingSource<Int, LiveData<List<ListStoryItem>>>() {
    companion object {
        fun snapshot(items: List<ListStoryItem>): PagingData<ListStoryItem> {
            return PagingData.from(items)
        }
    }
    override fun getRefreshKey(state: PagingState<Int, LiveData<List<ListStoryItem>>>): Int {
        return 0
    }
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<ListStoryItem>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}
val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}