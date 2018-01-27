/*
 * Copyright (C) 2018 Shehab Salah
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shehabsalah.movieappmvp.data.source.remote.serverResponse;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ShehabSalah on 1/8/18.
 *
 */
public class FailResponse {
    @SerializedName("result")
    private int fail_reason_id;
    @SerializedName("message")
    private String message;

    public FailResponse() {

    }

    public FailResponse(int fail, String message) {
        this.fail_reason_id = fail;
        this.message = message;
    }

    public int getFailReasonId() {
        return fail_reason_id;
    }

    public void setFailReasonId(int fail_reason_id) {
        this.fail_reason_id = fail_reason_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
