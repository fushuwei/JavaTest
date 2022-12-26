package com.mochousoft.minio;

import lombok.Data;

/**
 * 文件信息
 *
 * @author fushuwei
 */
@Data
public class FileInfo {

    /**
     * 文件名称
     */
    String fileName;

    /**
     * 是否目录
     */
    Boolean isDirectory;
}
