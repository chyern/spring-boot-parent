package com.chenyudan.spring.boot.core.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.read.listener.ReadListener;
import com.chenyudan.spring.boot.core.error.BaseError;

import java.io.File;
import java.util.List;

/**
 * Description: excel文件
 *
 * @author chenyu
 * @since 2023/6/8 10:07
 */
public class ExcelUtil {

    /**
     * 生成excel文件
     *
     * @param filePath  文件路径
     * @param sheetName 页签名称
     * @param clazz     对象
     * @param data      数据
     */
    public static <T> File generateExcel(String filePath, String sheetName, Class<T> clazz, List<T> data) {
        File file = new File(filePath);
        AssertUtil.isTrue(!file.exists(), BaseError.FILE_EXIST);
        EasyExcel.write(filePath, clazz).sheet(sheetName).doWrite(data);
        return file;
    }

    /**
     * 读取excel
     *
     * @param filePath 文件路径
     * @param clazz    读取对象
     */
    public static <T> List<T> readExcel(String filePath, Class<T> clazz) {
        return readExcel(filePath, 0, clazz);
    }

    /**
     * 读取excel
     *
     * @param filePath 文件路径
     * @param clazz    读取对象
     */
    public static <T> List<T> readExcel(String filePath, int sheetNo, Class<T> clazz) {
        return readExcel(filePath, sheetNo, clazz, new ExcelListener<>());
    }

    /**
     * 读取excel
     *
     * @param filePath     文件路径
     * @param readListener 监听器
     */
    public static <T> List<T> readExcel(String filePath, int sheetNo, Class<T> clazz, ReadListener<T> readListener) {
        return EasyExcel.read(filePath, clazz, readListener).sheet(sheetNo).doReadSync();
    }


    public static class ExcelListener<T> extends AnalysisEventListener<T> {

        @Override
        public void invoke(T data, AnalysisContext analysisContext) {
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        }

    }
}
