package tk.hb.english

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import tk.hb.english.data.db.HbDataBase
import tk.hb.english.data.db.entity.WordBean

/**
 * Created by HONGBO on 2020/7/27 18:45
 */
const val sharePreKey: String = "KEY_SHARE_PRE"
const val saveIndexKey: String = "KEY_SAVE_INDEX"

class WordViewModel : ViewModel() {

    val sharePre: SharedPreferences by lazy {
        MyApplication.getAppContext().getSharedPreferences(
            sharePreKey,
            Context.MODE_PRIVATE
        )
    }

    private var thisIndex: Int = 1

    private var showWord: MutableLiveData<WordBean> = MutableLiveData()

    fun goBack() {
        thisIndex--
        resetWord(-1)
    }

    fun goNext() {
        thisIndex++
        resetWord(1)
    }

    private fun resetWord(type: Int) {
        Thread(Runnable {
            if (thisIndex < 0) {
                thisIndex = 0
            }
            var data = HbDataBase.instance.wordDao()?.queryWord(thisIndex)
            if (data == null) {
                thisIndex = 1
                data = HbDataBase.instance.wordDao()?.queryWord(thisIndex)
            } else if (data.state == -1) {
                Handler(Looper.getMainLooper()).post {
                    if (type < 0) {
                        goBack()
                    } else {
                        goNext()
                    }
                }
            } else {
                Handler(Looper.getMainLooper()).post {
                    showWord.postValue(data)
                }
            }
        }).start()
    }

    fun getWord(): MutableLiveData<WordBean> {
        if (showWord.value == null) {
            resetWord(1)
        }
        return showWord
    }

    /**
     * 保存修改过的内容
     */
    fun saveWord(content: String) {
        Thread(Runnable {
            var wordBean: WordBean = showWord.value as WordBean
            wordBean.sentence = content
            HbDataBase.instance.wordDao()?.updateWord(wordBean)
        }).start()
    }

    /**
     * 删除当前词
     */
    fun delWord() {
        Thread(Runnable {
            var wordBean: WordBean = showWord.value as WordBean
            wordBean.state = -1
            HbDataBase.instance.wordDao()?.updateWord(wordBean)
            Handler(Looper.getMainLooper()).post {
                goNext()
            }
        }).start()
    }

    /**
     * 恢复所有词的初始化
     */
    fun restoreWord() {
        Thread(Runnable {
            HbDataBase.instance.wordDao()?.updateStateAll()
            thisIndex = 2
            goBack()
        }).start()
    }

    /**
     * 保存阅读记录
     */
    fun saveIndex() {
        sharePre.edit().putInt(saveIndexKey, thisIndex).commit()
    }

    /**
     * 恢复阅读记录
     */
    fun restoreIndex() {
        thisIndex = sharePre.getInt(saveIndexKey, 1)
    }
}
