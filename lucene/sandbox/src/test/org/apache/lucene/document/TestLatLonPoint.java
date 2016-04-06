begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.document
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|LuceneTestCase
import|;
end_import

begin_comment
comment|/**  * Simple tests for {@link LatLonPoint}  * TODO: move this lone test and remove class?  * */
end_comment

begin_class
DECL|class|TestLatLonPoint
specifier|public
class|class
name|TestLatLonPoint
extends|extends
name|LuceneTestCase
block|{
DECL|method|testToString
specifier|public
name|void
name|testToString
parameter_list|()
throws|throws
name|Exception
block|{
comment|// looks crazy due to lossiness
name|assertEquals
argument_list|(
literal|"LatLonPoint<field:18.313693958334625,-65.22744401358068>"
argument_list|,
operator|(
operator|new
name|LatLonPoint
argument_list|(
literal|"field"
argument_list|,
literal|18.313694
argument_list|,
operator|-
literal|65.227444
argument_list|)
operator|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// looks crazy due to lossiness
name|assertEquals
argument_list|(
literal|"field:[18.000000016763806 TO 18.999999999068677],[-65.9999999217689 TO -65.00000006519258]"
argument_list|,
name|LatLonPoint
operator|.
name|newBoxQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
operator|-
literal|66
argument_list|,
operator|-
literal|65
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// distance query does not quantize inputs
name|assertEquals
argument_list|(
literal|"field:18.0,19.0 +/- 25.0 meters"
argument_list|,
name|LatLonPoint
operator|.
name|newDistanceQuery
argument_list|(
literal|"field"
argument_list|,
literal|18
argument_list|,
literal|19
argument_list|,
literal|25
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// sort field
name|assertEquals
argument_list|(
literal|"<distance:\"field\" latitude=18.0 longitude=19.0>"
argument_list|,
name|LatLonPoint
operator|.
name|newDistanceSort
argument_list|(
literal|"field"
argument_list|,
literal|18.0
argument_list|,
literal|19.0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

