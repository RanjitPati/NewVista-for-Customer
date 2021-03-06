package com.jm.newvista.mvp.presenter;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.jm.newvista.bean.UserEntity;
import com.jm.newvista.mvp.base.BasePresenter;
import com.jm.newvista.mvp.model.MainModel;
import com.jm.newvista.mvp.view.MainView;
import com.jm.newvista.util.ImageUtil;
import com.jm.newvista.util.MessageServiceUtil;

/**
 * Created by Johnny on 2/6/2018.
 */

public class MainPresenter extends BasePresenter<MainModel, MainView> {
    MainModel mainModel;

    public MainPresenter() {
        mainModel = new MainModel();
        super.BasePresenter(mainModel);
    }

    @SuppressLint("StaticFieldLeak")
    public void updateNavigationView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new AsyncTask<Void, Void, UserEntity>() {
                    @Override
                    protected UserEntity doInBackground(Void... voids) {
                        UserEntity userEntity = mainModel.getCurrentUser();
                        if (userEntity != null) {
                            Log.v("onUpdateNavigationView", getClass() + userEntity.getEmail());
                            userEntity.setAvatar(ImageUtil.decode(userEntity.getAvatarStr()));
                            return userEntity;
                        } else {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(UserEntity userEntity) {
                        getView().onUpdateNavigationView(userEntity);
                    }
                }.execute();
            }
        }).start();
    }

    public void getMovieFromServer() {
        mainModel.getAndSaveMovie(new MainModel.MainModelCallbackListener() {
            @Override
            public void onSaveMovieFinish() {
                Log.v("getAndSaveMovie", getClass() + ", movie saved");
                getView().onNotifyMovieSaved();
            }

            @Override
            public void onDeleteData(int status) {
            }
        });
    }

    public void signOut() {
        mainModel.deleteAllData(new MainModel.MainModelCallbackListener() {
            @Override
            public void onSaveMovieFinish() {
            }

            @Override
            public void onDeleteData(int status) {
                if (status > 0) {
                    getView().onSignOutSuccess();
                } else {
                    getView().onSignOutFailure();
                }
            }
        });
    }

    public void sendLocalServerSocketInfoToWebServer() {
        mainModel.sendLocalPortInfoToWebServer(new MainModel.SendLocalPortListener() {
            @Override
            public void onSendLocalPortSuccess() {
                getView().onMakeToast("Local port information sent to server successfully " + MessageServiceUtil
                        .localPort);
            }

            @Override
            public void onSendLocalPortFailure() {
                getView().onMakeToast("Local port information didn't send to server");
            }
        });
    }
}
