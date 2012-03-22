begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.handler.dataimport
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|dataimport
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|nullValue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_class
DECL|class|DestroyCountCache
specifier|public
class|class
name|DestroyCountCache
extends|extends
name|SortedMapBackedCache
block|{
DECL|field|destroyed
specifier|static
name|Map
argument_list|<
name|DIHCache
argument_list|,
name|DIHCache
argument_list|>
name|destroyed
init|=
operator|new
name|IdentityHashMap
argument_list|<
name|DIHCache
argument_list|,
name|DIHCache
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|destroy
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|super
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertThat
argument_list|(
name|destroyed
operator|.
name|put
argument_list|(
name|this
argument_list|,
name|this
argument_list|)
argument_list|,
name|nullValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|DestroyCountCache
specifier|public
name|DestroyCountCache
parameter_list|()
block|{}
block|}
end_class

end_unit

