package org.fh.gae.das.mysql;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class MysqlRowData {
    private Map<String, String> after;

    private Map<String, String> before;
}