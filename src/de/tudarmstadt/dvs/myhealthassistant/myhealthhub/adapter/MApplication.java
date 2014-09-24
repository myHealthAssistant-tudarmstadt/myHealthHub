/* 
 * Copyright (C) 2014 TU Darmstadt, Hessen, Germany.
 * Department of Computer Science Databases and Distributed Systems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */ 
 
 package de.tudarmstadt.dvs.myhealthassistant.myhealthhub.adapter;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import de.tudarmstadt.dvs.myhealthassistant.myhealthhub.R;
import android.app.Application;
import android.content.Context;

@ReportsCrashes(formKey = "", // will not be used
				mailTo = "myHealthAssistant.tudarmstadt@googlemail.com",
				mode = ReportingInteractionMode.DIALOG,
                resToastText = R.string.crash_toast_text, // optional, displayed as soon as the crash occurs, before collecting data which can take a few seconds
                resDialogText = R.string.crash_dialog_text,
                resDialogIcon = android.R.drawable.ic_dialog_info, //optional. default is a warning sign
                resDialogTitle = R.string.crash_dialog_title, // optional. default is your application name
                //resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
                resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.
                 
				)
/**
 * Used to access context to getResource outside of activities
 * (nothing special here)
 * @author HieuHa
 *
 */
public class MApplication extends Application {
	private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        
		// The following line triggers the initialization of ACRA
		ACRA.init(this);
    }

    public static Context getContext(){
        return mContext;
    }
}