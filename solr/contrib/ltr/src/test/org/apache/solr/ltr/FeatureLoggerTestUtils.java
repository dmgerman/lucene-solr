begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.ltr
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|ltr
package|;
end_package

begin_class
DECL|class|FeatureLoggerTestUtils
specifier|public
class|class
name|FeatureLoggerTestUtils
block|{
DECL|method|toFeatureVector
specifier|public
specifier|static
name|String
name|toFeatureVector
parameter_list|(
name|String
modifier|...
name|keysAndValues
parameter_list|)
block|{
return|return
name|toFeatureVector
argument_list|(
name|CSVFeatureLogger
operator|.
name|DEFAULT_KEY_VALUE_SEPARATOR
argument_list|,
name|CSVFeatureLogger
operator|.
name|DEFAULT_FEATURE_SEPARATOR
argument_list|,
name|keysAndValues
argument_list|)
return|;
block|}
DECL|method|toFeatureVector
specifier|public
specifier|static
name|String
name|toFeatureVector
parameter_list|(
name|char
name|keyValueSeparator
parameter_list|,
name|char
name|featureSeparator
parameter_list|,
name|String
modifier|...
name|keysAndValues
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|keysAndValues
operator|.
name|length
operator|/
literal|2
operator|*
literal|3
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|ii
init|=
literal|0
init|;
name|ii
operator|+
literal|1
operator|<
name|keysAndValues
operator|.
name|length
condition|;
name|ii
operator|+=
literal|2
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|keysAndValues
index|[
name|ii
index|]
argument_list|)
operator|.
name|append
argument_list|(
name|keyValueSeparator
argument_list|)
operator|.
name|append
argument_list|(
name|keysAndValues
index|[
name|ii
operator|+
literal|1
index|]
argument_list|)
operator|.
name|append
argument_list|(
name|featureSeparator
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|features
init|=
operator|(
name|sb
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|?
name|sb
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
else|:
literal|""
operator|)
decl_stmt|;
return|return
name|features
return|;
block|}
block|}
end_class

end_unit

