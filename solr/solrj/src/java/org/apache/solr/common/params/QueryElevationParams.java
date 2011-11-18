begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.solr.common.params
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|params
package|;
end_package

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_comment
comment|/**  * Parameters used with the QueryElevationComponent  *  **/
end_comment

begin_interface
DECL|interface|QueryElevationParams
specifier|public
interface|interface
name|QueryElevationParams
block|{
DECL|field|ENABLE
name|String
name|ENABLE
init|=
literal|"enableElevation"
decl_stmt|;
DECL|field|EXCLUSIVE
name|String
name|EXCLUSIVE
init|=
literal|"exclusive"
decl_stmt|;
DECL|field|FORCE_ELEVATION
name|String
name|FORCE_ELEVATION
init|=
literal|"forceElevation"
decl_stmt|;
comment|/**    * The name of the field that editorial results will be written out as when using the QueryElevationComponent, which    * automatically configures the EditorialMarkerFactory.  The default name is "elevated"    *<br/>    * See http://wiki.apache.org/solr/DocTransformers    */
DECL|field|EDITORIAL_MARKER_FIELD_NAME
name|String
name|EDITORIAL_MARKER_FIELD_NAME
init|=
literal|"editorialMarkerFieldName"
decl_stmt|;
block|}
end_interface

end_unit

