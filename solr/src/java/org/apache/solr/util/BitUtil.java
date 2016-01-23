begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.solr.util
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  A variety of high efficiencly bit twiddling routines.  * @deprecated Use {@link org.apache.lucene.util.BitUtil} directly  * @version $Id$  */
end_comment

begin_class
DECL|class|BitUtil
specifier|public
class|class
name|BitUtil
extends|extends
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BitUtil
block|{
comment|// just inherit for backwards-compatibility reasons
block|}
end_class

end_unit

