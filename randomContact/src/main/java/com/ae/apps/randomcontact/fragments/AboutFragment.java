/*
 * Copyright 2013 Midhun Harikumar
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ae.apps.randomcontact.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ae.apps.common.utils.DialogUtils;
import com.ae.apps.randomcontact.R;

/**
 * The aboutActivity
 *
 * @author MidhunHK
 */
public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View layout = inflater.inflate(R.layout.activity_about, container, false);

        final Context context = getActivity();

        // Show license
        View license = layout.findViewById(R.id.viewLicense);
        license.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DialogUtils.showMaterialInfoDialog(context, R.string.action_license, R.string.str_license,
                        android.R.string.ok);
            }
        });

        // View the application's source
        View viewSource = layout.findViewById(R.id.viewSourceCode);
        viewSource.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String url = getString(R.string.github_source_url);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        });

        return layout;
    }

}
