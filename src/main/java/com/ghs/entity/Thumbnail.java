package com.ghs.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
/**
 * 缩略图对象
 */
public class Thumbnail {
        private String url;
        private String detailUrl;
        private String bigUrl;
}
