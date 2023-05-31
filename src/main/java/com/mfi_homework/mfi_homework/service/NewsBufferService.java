package com.mfi_homework.mfi_homework.service;


import com.mfi_homework.mfi_homework.entity.NewsItem;

import java.util.List;
import java.util.Map;

public interface NewsBufferService {

    void putAndSave(Map<String, List<NewsItem>> map);

    void saveAndClear();
}
