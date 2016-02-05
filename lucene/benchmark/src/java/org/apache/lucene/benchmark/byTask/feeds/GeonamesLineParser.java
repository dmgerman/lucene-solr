begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.benchmark.byTask.feeds
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|benchmark
operator|.
name|byTask
operator|.
name|feeds
package|;
end_package

begin_comment
comment|/**  * A line parser for Geonames.org data.  * See<a href="http://download.geonames.org/export/dump/readme.txt">'geoname' table</a>.  * Requires {@link SpatialDocMaker}.  */
end_comment

begin_class
DECL|class|GeonamesLineParser
specifier|public
class|class
name|GeonamesLineParser
extends|extends
name|LineDocSource
operator|.
name|LineParser
block|{
comment|/** This header will be ignored; the geonames format is fixed and doesn't have a header line. */
DECL|method|GeonamesLineParser
specifier|public
name|GeonamesLineParser
parameter_list|(
name|String
index|[]
name|header
parameter_list|)
block|{
name|super
argument_list|(
name|header
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|parseLine
specifier|public
name|void
name|parseLine
parameter_list|(
name|DocData
name|docData
parameter_list|,
name|String
name|line
parameter_list|)
block|{
name|String
index|[]
name|parts
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\\t"
argument_list|,
literal|7
argument_list|)
decl_stmt|;
comment|//no more than first 6 fields needed
comment|//    Sample data line:
comment|// 3578267, Morne du Vitet, Morne du Vitet, 17.88333, -62.8, ...
comment|// ID, Name, Alternate name (unused), Lat, Lon, ...
name|docData
operator|.
name|setID
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|parts
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
comment|//note: overwrites ID assigned by LineDocSource
name|docData
operator|.
name|setName
argument_list|(
name|parts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
name|String
name|latitude
init|=
name|parts
index|[
literal|4
index|]
decl_stmt|;
name|String
name|longitude
init|=
name|parts
index|[
literal|5
index|]
decl_stmt|;
name|docData
operator|.
name|setBody
argument_list|(
literal|"POINT("
operator|+
name|longitude
operator|+
literal|" "
operator|+
name|latitude
operator|+
literal|")"
argument_list|)
expr_stmt|;
comment|//WKT is x y order
block|}
block|}
end_class

end_unit

