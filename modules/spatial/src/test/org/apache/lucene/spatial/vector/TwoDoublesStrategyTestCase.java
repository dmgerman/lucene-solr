begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.vector
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|vector
package|;
end_package

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|SpatialContext
import|;
end_import

begin_import
import|import
name|com
operator|.
name|spatial4j
operator|.
name|core
operator|.
name|context
operator|.
name|simple
operator|.
name|SimpleSpatialContext
import|;
end_import

begin_class
DECL|class|TwoDoublesStrategyTestCase
specifier|public
class|class
name|TwoDoublesStrategyTestCase
extends|extends
name|BaseTwoDoublesStrategyTestCase
block|{
annotation|@
name|Override
DECL|method|getSpatialContext
specifier|protected
name|SpatialContext
name|getSpatialContext
parameter_list|()
block|{
return|return
name|SimpleSpatialContext
operator|.
name|GEO_KM
return|;
block|}
block|}
end_class

end_unit

