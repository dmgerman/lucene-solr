begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.util
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
package|;
end_package

begin_comment
comment|/** time unit constants for use in annotations. */
end_comment

begin_class
DECL|class|TimeUnits
specifier|public
specifier|final
class|class
name|TimeUnits
block|{
DECL|method|TimeUnits
specifier|private
name|TimeUnits
parameter_list|()
block|{}
comment|/** 1 second in milliseconds */
DECL|field|SECOND
specifier|public
specifier|static
specifier|final
name|int
name|SECOND
init|=
literal|1000
decl_stmt|;
comment|/** 1 minute in milliseconds */
DECL|field|MINUTE
specifier|public
specifier|static
specifier|final
name|int
name|MINUTE
init|=
literal|60
operator|*
name|SECOND
decl_stmt|;
comment|/** 1 hour in milliseconds */
DECL|field|HOUR
specifier|public
specifier|static
specifier|final
name|int
name|HOUR
init|=
literal|60
operator|*
name|MINUTE
decl_stmt|;
block|}
end_class

end_unit

