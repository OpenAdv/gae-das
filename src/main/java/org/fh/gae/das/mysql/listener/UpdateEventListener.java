package org.fh.gae.das.mysql.listener;

import com.github.shyiko.mysql.binlog.event.EventType;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.das.mysql.MysqlRowData;
import org.fh.gae.das.sender.file.FileSender;
import org.fh.gae.das.template.DasTable;
import org.fh.gae.das.template.OpType;
import org.fh.gae.das.template.TemplateHolder;
import org.fh.gae.das.template.level.DasLevel;
import org.fh.gae.das.template.level.TextDasLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class UpdateEventListener extends AggregationListener {
    @Autowired
    private TemplateHolder holder;

    @Autowired
    private FileSender store;

    // @Autowired
    // private KafkaSender kafkaSender;

    public UpdateEventListener() {
        super(EventType.UPDATE_ROWS);
    }

    @Override
    protected String getDbName() {
        return "gae-das";
    }

    @Override
    protected void doEvent(MysqlRowData eventData) {
        DasTable table = eventData.getTable();

        // 构造层级对象
        DasLevel level = new TextDasLevel();
        level.setTable(table);
        level.setOpType(OpType.UPDATE);

        // 取出模板中UPDATE操作对应的字段列表
        List<String> fieldList = table.getOpTypeFieldSetMap().get(OpType.UPDATE);
        if (null == fieldList) {
            log.warn("UPDATE not support for {}", table.getTableName());
            return;
        }

        for (Map.Entry<String, String> entry : eventData.getAfter().entrySet()) {
            String colName = entry.getKey();
            String colValue = entry.getValue();


            level.getFieldValueMap().put(colName, colValue);
        }

        store.send(level);
        // kafkaSender.send(level);
    }

    @Override
    protected TemplateHolder getTemplateHolder() {
        return this.holder;
    }
}
