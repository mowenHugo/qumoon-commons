/*
 * Copyright (c) 2013 Raycloud.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.trilemon.commons;

import com.google.common.collect.Lists;
import com.google.protobuf.Descriptors;
import org.apache.commons.beanutils.PropertyUtils;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 简单封装Dozer, 实现深度转换Bean<->Bean的Mapper.实现:
 * <p/>
 * <ul> <li>持有Mapper的单例.</li> <li>返回值类型转换.</li> <li>批量转换Collection中的所有对象.</li> <li>区分创建新的B对象与将对象A值复制到已存在的B对象两种函数.</li>
 * </ul>
 *
 * @author kevin
 */
public class BeanMapper {
    private static Logger logger = LoggerFactory.getLogger(BeanMapper.class);
    /**
     * 持有Dozer单例, 避免重复创建DozerMapper消耗资源.
     */
    private static DozerBeanMapper dozer = new DozerBeanMapper();

    static {
        dozer.setMappingFiles(Lists.newArrayList("dozer/dozer_Map_to_TopSession.xml"));
    }

    /**
     * 基于Dozer转换对象的类型.
     */
    public static <T> T map(Object source, Class<T> destinationClass) {
        return dozer.map(source, destinationClass);
    }

    /**
     * 基于Dozer转换Collection中对象的类型.
     */
    public static <T> List<T> mapList(Collection sourceList, Class<T> destinationClass) {
        List<T> destinationList = Lists.newArrayList();
        for (Object sourceObject : sourceList) {
            T destinationObject = dozer.map(sourceObject, destinationClass);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }

    /**
     * 基于Dozer将对象A的值拷贝到对象B中.
     */
    public static void copy(Object source, Object destinationObject) {
        dozer.map(source, destinationObject);
    }

    public static <T extends com.google.protobuf.GeneratedMessage> T convertBean2Proto(com.google.protobuf
                                                                                               .GeneratedMessage.Builder message,
                                                                                       Object srcObject) throws Exception {
        for (Field srcField : srcObject.getClass().getDeclaredFields()) {
            Object value = PropertyUtils.getProperty(srcObject, srcField.getName());
            if (value == null) continue;
            Descriptors.FieldDescriptor fd = message.getDescriptorForType().findFieldByName(srcField.getName());
            if (null == fd) continue;
            if (srcField.getType() == BigDecimal.class) {
                message.setField(fd, ((BigDecimal) value).toString());
                continue;
            } else {
                if (srcField.getType() == Date.class) {
                    message.setField(fd, ((Date) value).getTime());
                    continue;
                } else {
                    message.setField(fd, value);
                }
            }
        }
        return (T) message.build();
    }

    public static <T> T convertProto2Bean(com.google.protobuf.GeneratedMessage message, T descObject, Class srcClass) {
        for (Field srcField : srcClass.getDeclaredFields()) {
            Descriptors.FieldDescriptor fd = message.getDescriptorForType().findFieldByName(srcField.getName());
            if (null == fd) continue;
            try {
                String fieldStrValue = String.valueOf(message.getField(fd));
                if (fieldStrValue.isEmpty()) {
                    continue;
                }
                if (srcField.getType() == BigDecimal.class) {
                    PropertyUtils.setProperty(descObject, srcField.getName(), new BigDecimal(fieldStrValue));
                } else {
                    if (srcField.getType() == Date.class) {
                        PropertyUtils.setProperty(descObject, srcField.getName(), new Date((Long) (message.getField
                                (fd))));
                    } else {
                        if (srcField.getType() == Byte.class) {
                            PropertyUtils.setProperty(descObject, srcField.getName(), Byte.valueOf(fieldStrValue));
                        } else {
                            PropertyUtils.setProperty(descObject, srcField.getName(), message.getField(fd));
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
            } finally {
                continue;
            }
        }
        return descObject;
    }
}
