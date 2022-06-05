package com.ych.ychbase.constant

/**
 * 常量类
 *
 * @author lmy
 */
interface YchConst {
    interface Common {
        companion object {
//            const val DESIGN_WIDTH: Int = 750
            const val DESIGN_HEIGHT: Int = 1334
        }
    }

    interface Camera {
        companion object {
            const val CAMERA_REQUEST_CODE: Int = 201
            const val GALLERY_REQUEST_CODE: Int = 202
            const val CROP_REQUEST_CODE = 203

            const val CROP_ERROR_CODE: Int = 404
        }
    }
}