package com.aokiji.watermarkhelper

object Settings {

    // * * * * * * Intent Key * * * * * *

    // MainActivity
    const val TYPE_PERMISSION_ERROR = 0
    const val TYPE_DATA_ERROR = 1
    // AddWatermarkActivity
    const val INTENT_KEY_CHOSE_PHOTO = 1001
    const val INTENT_KEY_TAKE_PHOTO = 1002
    // SettingActivity
    const val REQUEST_CODE_EDIT_WATERMARK = 1003
    // EditActivity
    const val INTENT_KEY_INPUT_CONTENT = "INTENT_KEY_INPUT_CONTENT"
    const val INTENT_KEY_OUTPUT_CONTENT = "INTENT_KEY_OUTPUT_CONTENT"
    // PhotoDetailsActivity
    const val INTENT_KEY_IMAGES = "INTENT_KEY_IMAGES"

    // * * * * * * Intent Key * * * * * *

    // * * * * * * MMKV Key * * * * * *

    // SettingActivity
    const val MMKV_KEY_WATERMARK = "MMKV_KEY_WATERMARK"
    const val MMKV_KEY_WATERMARK_COLOR = "MMKV_KEY_WATERMARK_COLOR"
    const val MMKV_KEY_WATERMARK_TEXT_SIZE = "MMKV_KEY_WATERMARK_TEXT_SIZE"

    // * * * * * * MMKV Key * * * * * *
}