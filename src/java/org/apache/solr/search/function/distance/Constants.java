begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.search.function.distance
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|function
operator|.
name|distance
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  *  *  **/
end_comment

begin_interface
DECL|interface|Constants
specifier|public
interface|interface
name|Constants
block|{
DECL|field|EARTH_RADIUS_KM
specifier|public
specifier|static
specifier|final
name|double
name|EARTH_RADIUS_KM
init|=
literal|6378.160187
decl_stmt|;
DECL|field|EARTH_RADIUS_MI
specifier|public
specifier|static
specifier|final
name|double
name|EARTH_RADIUS_MI
init|=
literal|3963.205
decl_stmt|;
block|}
end_interface

end_unit

