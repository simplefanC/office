package com.simplefanc.office.message.dao;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.simplefanc.office.message.entity.MessageEntity;
import com.simplefanc.office.message.entity.MessageRefEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author chenfan
 */
@Repository
public class MessageDao {
    @Autowired
    private MongoTemplate mongoTemplate;

    public String insert(MessageEntity entity) {
        //把北京时间转换成格林尼治时间 (MongoDB存的是格林尼治时间)
        Date sendTime = entity.getSendTime();
        sendTime = DateUtil.offset(sendTime, DateField.HOUR, 8);
        entity.setSendTime(sendTime);
        entity = mongoTemplate.save(entity);
        return entity.get_id();
    }

    public List<HashMap> searchMessageByPage(int userId, long start, int length) {
        JSONObject json = new JSONObject();
        json.set("$toString", "$_id");
        Aggregation aggregation = Aggregation.newAggregation(
                //_id字段toString类型转换 保存到id
                Aggregation.addFields().addField("id").withValue(json).build(),
                //关联message_ref的messageId字段 查询结果引用字段:ref
                Aggregation.lookup("message_ref", "id", "messageId", "ref"),
                //ref.receiverId = userId
                Aggregation.match(Criteria.where("ref.receiverId").is(userId)),
                //sendTime降序
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "sendTime")),
                //分页
                Aggregation.skip(start),
                Aggregation.limit(length)
        );
        AggregationResults<HashMap> results = mongoTemplate.aggregate(aggregation, "message", HashMap.class);
        List<HashMap> list = results.getMappedResults();
        list.forEach(one -> {
            List<MessageRefEntity> refList = (List<MessageRefEntity>) one.get("ref");
            MessageRefEntity entity = refList.get(0);
            boolean readFlag = entity.getReadFlag();
            String refId = entity.get_id();

//            List<LinkedHashMap> refList = (List<LinkedHashMap>) one.get("ref");
//            LinkedHashMap map = refList.get(0);//todo MessageRefEntity cannot be cast to java.util.LinkedHashMap
//            Boolean readFlag = (Boolean) map.get("readFlag");
//            String refId = map.get("_id").toString();

            one.put("readFlag", readFlag);
            one.put("refId", refId);
            one.remove("ref");
            one.remove("_id");
            //把格林尼治时间转换成北京时间
            Date sendTime = (Date) one.get("sendTime");
            sendTime = DateUtil.offset(sendTime, DateField.HOUR, -8);
            String today = DateUtil.today();
            //如果是今天的消息，只显示发送时间，不需要显示日期
            if (today.equals(DateUtil.date(sendTime).toDateStr())) {
                one.put("sendTime", DateUtil.format(sendTime, "HH:mm"));
            }
            //如果是以往的消息，只显示日期，不显示发送时间
            else {
                one.put("sendTime", DateUtil.format(sendTime, "yyyy/MM/dd"));
            }
        });
        return list;
    }

    public HashMap searchMessageById(String id) {
        HashMap map = mongoTemplate.findById(id, HashMap.class, "message");
        //把格林尼治时间转换成北京时间
        Date sendTime = (Date) map.get("sendTime");
        sendTime = DateUtil.date(sendTime).offset(DateField.HOUR, -8);
        map.replace("sendTime", DateUtil.format(sendTime, "yyyy-MM-dd HH:mm"));
        return map;
    }
}