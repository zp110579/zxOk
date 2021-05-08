package com.zee.http.bean;

import com.zee.http.request.OKHttpParams;

import java.io.File;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadFileParams extends OKHttpParams {

    private String message;
    private byte[] dataByte;
    private MediaType mediaType;
    private RequestBody requestBody;
    private LinkedHashMap<String, List<FileWrapper>> fileParamsMap = new LinkedHashMap<>();

    public UploadFileParams upLoadBytes(byte[] bs) {
        this.dataByte = bs;
        this.mediaType = MediaType.parse("application/octet-stream");
        return this;
    }


    public UploadFileParams upLoadString(String string) {
        this.message = string;
        this.mediaType = MediaType.parse("text/plain;charset=utf-8");
        return this;
    }


    public UploadFileParams upLoadJson(String json) {
        this.message = json;
        this.mediaType = MediaType.parse("application/json;charset=utf-8");
        return this;
    }

    public UploadFileParams upLoadFile(String key, File file) {
        String fileName = file.getName();
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String path = fileName.replace("#", "");
        String contentType = fileNameMap.getContentTypeFor(path);
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        MediaType mediaType = MediaType.parse(contentType);

        if (key != null) {
            List<FileWrapper> fileWrappers = fileParamsMap.get(key);
            if (fileWrappers == null) {
                fileWrappers = new ArrayList<>();
                fileParamsMap.put(key, fileWrappers);
            }
            fileWrappers.add(new FileWrapper(file, fileName, mediaType));
        }
        return this;
    }

    public UploadFileParams upLoadFiles(String key, List<File> files) {
        if (key != null && files != null && !files.isEmpty()) {
            for (File file : files) {
                upLoadFile(key, file);
            }
        }
        return this;
    }

    @Override
    public RequestBody getRequestBody() {
        if (requestBody != null) {
            return requestBody;
        }
        if (message != null && mediaType != null) {
            return RequestBody.create(mediaType, message);
        }
        if (dataByte != null && mediaType != null) {
            return RequestBody.create(mediaType, dataByte);
        }

        if (fileParamsMap.isEmpty()) {
            //表单提交，没有文件
            FormBody.Builder bodyBuilder = new FormBody.Builder();
            for (String key : mParamsHashMap.keySet()) {
                String urlValues = mParamsHashMap.get(key);
                bodyBuilder.add(key, urlValues);
            }
            return bodyBuilder.build();
        } else {
            MultipartBody.Builder multipartBodybuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);

            if (!mParamsHashMap.isEmpty()) {//键值对
                for (Map.Entry<String, String> entry : mParamsHashMap.entrySet()) {
                    String urlValues = entry.getValue();
                    multipartBodybuilder.addFormDataPart(entry.getKey(), urlValues);

                }
            }

            if (!fileParamsMap.isEmpty()) {//文件
                for (Map.Entry<String, List<FileWrapper>> entry : fileParamsMap.entrySet()) {
                    List<FileWrapper> fileValues = entry.getValue();
                    for (FileWrapper fileWrapper : fileValues) {
                        RequestBody fileBody = RequestBody.create(fileWrapper.contentType, fileWrapper.file);
                        multipartBodybuilder.addFormDataPart(entry.getKey(), fileWrapper.fileName, fileBody);
                    }
                }
            }
            return multipartBodybuilder.build();
        }
    }

    public static class FileWrapper {
        public File file;
        public String fileName;
        public MediaType contentType;
        public long fileSize;

        public FileWrapper(File file, String fileName, MediaType contentType) {
            this.file = file;
            this.fileName = fileName;
            this.contentType = contentType;
            this.fileSize = file.length();
        }

        @Override
        public String toString() {
            return "FileWrapper{" + "file=" + file + ", fileName='" + fileName + ", contentType=" + contentType + ", fileSize=" + fileSize + '}';
        }
    }
}
