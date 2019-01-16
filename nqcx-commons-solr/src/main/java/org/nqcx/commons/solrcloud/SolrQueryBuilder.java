/*
 * Copyright 2017 nqcx.org All right reserved. This software is the
 * confidential and proprietary information of nqcx.org ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with nqcx.org.
 */

package org.nqcx.commons.solrcloud;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.nqcx.commons.lang.o.DTO;
import org.nqcx.commons.solr.SolrSort;
import org.nqcx.commons.util.date.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author Jiangsiqi on 2017/8/17 19:18.
 */
public class SolrQueryBuilder {
	private final static Logger logger = LoggerFactory.getLogger(SolrQueryBuilder.class);
    private final static String SOLR_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    /**
     * 根据传入的参数构造查询条件
     * 根据keyword的值是否存在调用不同的方法来查询
     * 1.如果keyword值存在，调用权重提示的方式来查询(查询范围为:书名，作者，作者笔名，出版社，版权授权方)
     * 2.如果keyword值不存在，则按照原来的方法来调用
     * @param dto
     * @param query
     * @return
     */
    public static SolrQuery dto2query(DTO dto, SolrQuery query){
    	  if (dto == null)
              return null;
    	  if (query == null)
              query = new SolrQuery();
    	  Map<String, Object> fields = dto.getParamsMap();
    	  if(fields != null)
    		  logger.info(">>>SolrQuery Parameter:"+fields.toString());
    	  else
    		  logger.info("SolrQuery Parameter is null");
    	  if(fields != null&&fields.get("text_full")!= null){
    		  logger.info("Query By Dismax type");
    		  query = dto2queryDismax(dto,query);
          } else {
        	  logger.info("Query By Default type");
        	  query = dto2queryDefault(dto,query);
          }
    	  logger.info(">>>SolrQuery:"+query.toQueryString());
    	  return query;
    }
    
    /**
     * 按照默认的方式构建SolrQuery
     * @param dto
     * @param query
     * @return
     */
    public static SolrQuery dto2queryDefault(DTO dto, SolrQuery query) {

        if (dto == null)
            return null;

        if (query == null)
            query = new SolrQuery();

        Map<String, Object> fields = dto.getParamsMap();

        if (fields == null) {
            query = new SolrQuery().setQuery("*:*");
        } else {
            StringBuffer sb = new StringBuffer();
            Object object = null;
            for (Map.Entry<String, Object> field : fields.entrySet()) {
                object = field.getValue();
                if (object == null)
                    continue;

                if (sb.length() > 0)
                    sb.append(" AND ");

                if (object instanceof SolrList) {
                    SolrList solrList = (SolrList) object;
                    if (solrList != null) {
                        sb.append(solrList.getQueryString(field.getKey()));
                    }
                } else if (object instanceof SolrNull) {
                    SolrNull solrNull = (SolrNull) object;
                    if (solrNull.isTrue()) {
                        sb.append("-");
                    }
                    sb.append(field.getKey() + ":" + "[\"\" TO * ]");
                } else if (object instanceof SolrDate) {
                    SolrDate solrDate = (SolrDate) object;

                    if (solrDate != null && StringUtils.isNotBlank(field.getKey())
                            && (solrDate.getBegintime() != null || solrDate.getEndtime() != null)) {
                        String b = "*";
                        String e = "*";
                        if (solrDate.getBegintime() != null)
                            b = DateFormatUtils.format(solrDate.getBegintime(), SOLR_DATE_PATTERN);
                        if (solrDate.getEndtime() != null)
                            e = DateFormatUtils.format(solrDate.getEndtime(), SOLR_DATE_PATTERN);

                        sb.append(MessageFormat.format(" {0}:[{1} TO {2}] ", field.getKey(), b, e));
                    }
                } else
                    sb.append(field.getKey() + ":" + object);
            }
            query.setQuery(sb.toString());
        }

        // 分页
        if (dto.getPage() != null) {
            query.setStart((int) dto.getPage().getStartIndex());
            query.setRows((int) dto.getPage().getPageSize());
        }

        // 排序
        if (dto.getSort() != null && dto.getSort() instanceof SolrSort) {
            query.clearSorts();
            SolrSort ss = (SolrSort) dto.getSort();
            List<SolrQuery.SortClause> sortClauseList = null;
            if(ss != null && (sortClauseList = ss.getSearchOrder()) != null && sortClauseList.size() > 0) {
                for (SolrQuery.SortClause sc : sortClauseList) {
                    if(sc == null)
                        continue;
                    query.addSort(sc);
                }
            }
        }
        return query;
    }
    /**
     * 当text_full不为空时，采用权重的方式Dismax构建SolrQuery
     * @param dto
     * @param query
     * @return
     */
    public static SolrQuery dto2queryDismax(DTO dto, SolrQuery query) {

        if (dto == null)
            return null;

        if (query == null)
            query = new SolrQuery();
        
        query.set("defType","dismax");
        query.set("qf","name^200.0 author^20.0 authorPseudonym^20.0 publisher^2.0 authorizeName^2.0 announcer^2.0");

        Map<String, Object> fields = dto.getParamsMap();

        if (fields == null) {
            query = new SolrQuery().setQuery("*:*");
        } else {
        	StringBuffer sbQuery = new StringBuffer();
            StringBuffer sb = new StringBuffer();
            Object object = null;
            for (Map.Entry<String, Object> field : fields.entrySet()) {
                object = field.getValue();
                if (object == null)
                    continue;
                if(field.getKey().equals("text_full")){
                	sbQuery.append(object);
                }else{
	                if (sb.length() > 0)
	                    sb.append(" AND ");
	
	                if (object instanceof SolrList) {
	                    SolrList solrList = (SolrList) object;
	                    if (solrList != null) {
	                        sb.append(solrList.getQueryString(field.getKey()));
	                    }
	                } else if (object instanceof SolrNull) {
	                    SolrNull solrNull = (SolrNull) object;
	                    if (solrNull.isTrue()) {
	                        sb.append("-");
	                    }
	                    sb.append(field.getKey() + ":" + "[\"\" TO * ]");
	                } else if (object instanceof SolrDate) {
	                    SolrDate solrDate = (SolrDate) object;
	
	                    if (solrDate != null && StringUtils.isNotBlank(field.getKey())
	                            && (solrDate.getBegintime() != null || solrDate.getEndtime() != null)) {
	                        String b = "*";
	                        String e = "*";
	                        if (solrDate.getBegintime() != null)
	                            b = DateFormatUtils.format(solrDate.getBegintime(), SOLR_DATE_PATTERN);
	                        if (solrDate.getEndtime() != null)
	                            e = DateFormatUtils.format(solrDate.getEndtime(), SOLR_DATE_PATTERN);
	
	                        sb.append(MessageFormat.format(" {0}:[{1} TO {2}] ", field.getKey(), b, e));
	                    }
	                }else
	                    sb.append(field.getKey() + ":" + object);
                }
            }
            query.setQuery(sbQuery.toString());
            if(sb.length() > 0){
                query.setFilterQueries(sb.toString());
            }
        }

        // 分页
        if (dto.getPage() != null) {
            query.setStart((int) dto.getPage().getStartIndex());
            query.setRows((int) dto.getPage().getPageSize());
        }

        // 排序
        if (dto.getSort() != null && dto.getSort() instanceof SolrSort) {
            query.clearSorts();
            SolrSort ss = (SolrSort) dto.getSort();
            List<SolrQuery.SortClause> sortClauseList = null;
            if(ss != null && (sortClauseList = ss.getSearchOrder()) != null && sortClauseList.size() > 0) {
                for (SolrQuery.SortClause sc : sortClauseList) {
                    if(sc == null)
                        continue;
                    query.addSort(sc);
                }
            }
        }
        return query;
    }

    /**
     * @param dto
     * @return
     */
    public static SolrQuery dto2query(DTO dto) {
        return dto2query(dto, null);
    }

}
