package demo.domains;

import javax.web.doc.annotation.ApidocElement;
import javax.web.doc.AbstractResponsePage;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import demo.domains.*;

/**
 * copyright rnkrsoft.com 
 */
public class QueryResponse extends AbstractResponsePage<QueryResponse.RecordVO> {

    public static class RecordVO implements Serializable {
        @ApidocElement(value = "姓名", required = true, minLen = 0, maxLen = 255)
        String name;

        @ApidocElement(value = "年龄", required = false, minLen = 0, maxLen = 255)
        Integer age;

        @ApidocElement(value = "大小", required = true, minLen = 0, maxLen = 255)
        int size;

        @ApidocElement(value = "记录")
        Record1VO record1VO;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return this.age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public int getSize() {
            return this.size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public Record1VO getRecord1VO() {
            return this.record1VO;
        }

        public void setRecord1VO(Record1VO record1VO) {
            this.record1VO = record1VO;
        }


        public static class Record1VO implements Serializable {
            @ApidocElement(value = "大小", required = true, minLen = 0, maxLen = 255)
            int size;

            @ApidocElement(value = "Record2VO")
            Record2VO record2VO;

            /**
             * MAN1 男人
             * WOMAN2 女人
             */
            @ApidocElement(value = "性别", required = true, minLen = 0, maxLen = 255)
            String sex;

            /**
             * 1 男人
             * 2 女人
             */
            @ApidocElement(value = "性别1", required = true, minLen = 0, maxLen = 255)
            int sex1;

            public int getSize() {
                return this.size;
            }

            public void setSize(int size) {
                this.size = size;
            }

            public Record2VO getRecord2VO() {
                return this.record2VO;
            }

            public void setRecord2VO(Record2VO record2VO) {
                this.record2VO = record2VO;
            }

            public String getSex() {
                return this.sex;
            }

            public void setSex(String sex) {
                this.sex = sex;
            }

            public int getSex1() {
                return this.sex1;
            }

            public void setSex1(int sex1) {
                this.sex1 = sex1;
            }


            public static class Record2VO implements Serializable {
                @ApidocElement(value = "大小", required = true, minLen = 0, maxLen = 255)
                int size;

                @ApidocElement(value = "Record3VO")
                Record3VO record3VO;

                public int getSize() {
                    return this.size;
                }

                public void setSize(int size) {
                    this.size = size;
                }

                public Record3VO getRecord3VO() {
                    return this.record3VO;
                }

                public void setRecord3VO(Record3VO record3VO) {
                    this.record3VO = record3VO;
                }


                public static class Record3VO implements Serializable {
                    @ApidocElement(value = "大小", required = true, minLen = 0, maxLen = 255)
                    int size;

                    /**
                     * MAN1 男人
                     * WOMAN2 女人
                     */
                    @ApidocElement(value = "性别", required = true, minLen = 0, maxLen = 255)
                    String sex;

                    /**
                     * 1 男人
                     * 2 女人
                     */
                    @ApidocElement(value = "性别1", required = true, minLen = 0, maxLen = 255)
                    int sex1;

                    public int getSize() {
                        return this.size;
                    }

                    public void setSize(int size) {
                        this.size = size;
                    }

                    public String getSex() {
                        return this.sex;
                    }

                    public void setSex(String sex) {
                        this.sex = sex;
                    }

                    public int getSex1() {
                        return this.sex1;
                    }

                    public void setSex1(int sex1) {
                        this.sex1 = sex1;
                    }

                }
            }
        }
    }
}
