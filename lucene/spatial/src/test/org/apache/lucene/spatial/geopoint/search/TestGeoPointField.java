begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.lucene.spatial.geopoint.search
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geopoint
operator|.
name|search
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
name|analysis
operator|.
name|MockAnalyzer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|StringField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|RandomIndexWriter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|IndexSearcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TopDocs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geopoint
operator|.
name|document
operator|.
name|GeoPointField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|geopoint
operator|.
name|document
operator|.
name|GeoPointField
operator|.
name|TermEncoding
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
operator|.
name|GeoRelationUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|spatial
operator|.
name|util
operator|.
name|GeoUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Directory
import|;
end_import

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
name|TestUtil
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_comment
comment|/** Simple tests for GeoPoint */
end_comment

begin_class
DECL|class|TestGeoPointField
specifier|public
class|class
name|TestGeoPointField
extends|extends
name|LuceneTestCase
block|{
DECL|field|directory
specifier|private
specifier|static
name|Directory
name|directory
init|=
literal|null
decl_stmt|;
DECL|field|reader
specifier|private
specifier|static
name|IndexReader
name|reader
init|=
literal|null
decl_stmt|;
DECL|field|searcher
specifier|private
specifier|static
name|IndexSearcher
name|searcher
init|=
literal|null
decl_stmt|;
DECL|field|FIELD_NAME
specifier|private
specifier|static
specifier|final
name|String
name|FIELD_NAME
init|=
literal|"point"
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|directory
operator|=
name|newDirectory
argument_list|()
expr_stmt|;
name|RandomIndexWriter
name|writer
init|=
operator|new
name|RandomIndexWriter
argument_list|(
name|random
argument_list|()
argument_list|,
name|directory
argument_list|,
name|newIndexWriterConfig
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
operator|.
name|setMaxBufferedDocs
argument_list|(
name|TestUtil
operator|.
name|nextInt
argument_list|(
name|random
argument_list|()
argument_list|,
literal|100
argument_list|,
literal|1000
argument_list|)
argument_list|)
operator|.
name|setMergePolicy
argument_list|(
name|newLogMergePolicy
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
comment|// this is a simple systematic test
name|GeoPointField
index|[]
name|pts
init|=
operator|new
name|GeoPointField
index|[]
block|{
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
literal|32.763420
argument_list|,
operator|-
literal|96.774
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|,
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
literal|32.7559529921407
argument_list|,
operator|-
literal|96.7759895324707
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|,
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
literal|32.77866942010977
argument_list|,
operator|-
literal|96.77701950073242
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|,
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
literal|32.7756745755423
argument_list|,
operator|-
literal|96.7706036567688
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|,
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
literal|27.703618681345585
argument_list|,
operator|-
literal|139.73458170890808
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|,
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
literal|32.94823588839368
argument_list|,
operator|-
literal|96.4538113027811
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|,
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
literal|33.06047141970814
argument_list|,
operator|-
literal|96.65084838867188
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|,
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
literal|32.778650
argument_list|,
operator|-
literal|96.7772
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|,
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
operator|-
literal|88.56029371730983
argument_list|,
operator|-
literal|177.23537676036358
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|,
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
literal|33.541429799076354
argument_list|,
operator|-
literal|26.779373834241003
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|,
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
literal|26.774024500421728
argument_list|,
operator|-
literal|77.35379276106497
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|,
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
operator|-
literal|90.0
argument_list|,
operator|-
literal|14.796283808944777
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|,
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
literal|32.94823588839368
argument_list|,
operator|-
literal|178.8538113027811
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|,
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
literal|32.94823588839368
argument_list|,
literal|178.8538113027811
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|,
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
literal|40.720611
argument_list|,
operator|-
literal|73.998776
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|,
operator|new
name|GeoPointField
argument_list|(
name|FIELD_NAME
argument_list|,
operator|-
literal|44.5
argument_list|,
operator|-
literal|179.5
argument_list|,
name|GeoPointField
operator|.
name|PREFIX_TYPE_NOT_STORED
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|GeoPointField
name|p
range|:
name|pts
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// add explicit multi-valued docs
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|pts
operator|.
name|length
condition|;
name|i
operator|+=
literal|2
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|pts
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|pts
index|[
name|i
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
comment|// index random string documents
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|random
argument_list|()
operator|.
name|nextInt
argument_list|(
literal|10
argument_list|)
condition|;
operator|++
name|i
control|)
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
literal|"string"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
name|writer
operator|.
name|addDocument
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|reader
operator|=
name|writer
operator|.
name|getReader
argument_list|()
expr_stmt|;
name|searcher
operator|=
name|newSearcher
argument_list|(
name|reader
argument_list|)
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|afterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|Exception
block|{
name|searcher
operator|=
literal|null
expr_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|reader
operator|=
literal|null
expr_stmt|;
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
name|directory
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|bboxQuery
specifier|private
name|TopDocs
name|bboxQuery
parameter_list|(
name|double
name|minLat
parameter_list|,
name|double
name|maxLat
parameter_list|,
name|double
name|minLon
parameter_list|,
name|double
name|maxLon
parameter_list|,
name|int
name|limit
parameter_list|)
throws|throws
name|Exception
block|{
name|GeoPointInBBoxQuery
name|q
init|=
operator|new
name|GeoPointInBBoxQuery
argument_list|(
name|FIELD_NAME
argument_list|,
name|TermEncoding
operator|.
name|PREFIX
argument_list|,
name|minLat
argument_list|,
name|maxLat
argument_list|,
name|minLon
argument_list|,
name|maxLon
argument_list|)
decl_stmt|;
return|return
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|limit
argument_list|)
return|;
block|}
DECL|method|polygonQuery
specifier|private
name|TopDocs
name|polygonQuery
parameter_list|(
name|double
index|[]
name|polyLats
parameter_list|,
name|double
index|[]
name|polyLons
parameter_list|,
name|int
name|limit
parameter_list|)
throws|throws
name|Exception
block|{
name|GeoPointInPolygonQuery
name|q
init|=
operator|new
name|GeoPointInPolygonQuery
argument_list|(
name|FIELD_NAME
argument_list|,
name|TermEncoding
operator|.
name|PREFIX
argument_list|,
name|polyLats
argument_list|,
name|polyLons
argument_list|)
decl_stmt|;
return|return
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|limit
argument_list|)
return|;
block|}
DECL|method|geoDistanceQuery
specifier|private
name|TopDocs
name|geoDistanceQuery
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|radius
parameter_list|,
name|int
name|limit
parameter_list|)
throws|throws
name|Exception
block|{
name|GeoPointDistanceQuery
name|q
init|=
operator|new
name|GeoPointDistanceQuery
argument_list|(
name|FIELD_NAME
argument_list|,
name|TermEncoding
operator|.
name|PREFIX
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|radius
argument_list|)
decl_stmt|;
return|return
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|limit
argument_list|)
return|;
block|}
DECL|method|geoDistanceRangeQuery
specifier|private
name|TopDocs
name|geoDistanceRangeQuery
parameter_list|(
name|double
name|lat
parameter_list|,
name|double
name|lon
parameter_list|,
name|double
name|minRadius
parameter_list|,
name|double
name|maxRadius
parameter_list|,
name|int
name|limit
parameter_list|)
throws|throws
name|Exception
block|{
name|GeoPointDistanceRangeQuery
name|q
init|=
operator|new
name|GeoPointDistanceRangeQuery
argument_list|(
name|FIELD_NAME
argument_list|,
name|TermEncoding
operator|.
name|PREFIX
argument_list|,
name|lat
argument_list|,
name|lon
argument_list|,
name|minRadius
argument_list|,
name|maxRadius
argument_list|)
decl_stmt|;
return|return
name|searcher
operator|.
name|search
argument_list|(
name|q
argument_list|,
name|limit
argument_list|)
return|;
block|}
DECL|method|testBBoxQuery
specifier|public
name|void
name|testBBoxQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocs
name|td
init|=
name|bboxQuery
argument_list|(
literal|32.778650
argument_list|,
literal|32.778950
argument_list|,
operator|-
literal|96.7772
argument_list|,
operator|-
literal|96.77690000
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"GeoBoundingBoxQuery failed"
argument_list|,
literal|4
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testPolyQuery
specifier|public
name|void
name|testPolyQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocs
name|td
init|=
name|polygonQuery
argument_list|(
operator|new
name|double
index|[]
block|{
literal|33.073130
block|,
literal|32.9942669
block|,
literal|32.938386
block|,
literal|33.0374494
block|,
literal|33.1369762
block|,
literal|33.1162747
block|,
literal|33.073130
block|,
literal|33.073130
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
operator|-
literal|96.7682647
block|,
operator|-
literal|96.8280029
block|,
operator|-
literal|96.6288757
block|,
operator|-
literal|96.4929199
block|,
operator|-
literal|96.6041564
block|,
operator|-
literal|96.7449188
block|,
operator|-
literal|96.76826477
block|,
operator|-
literal|96.7682647
block|}
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"GeoPolygonQuery failed"
argument_list|,
literal|2
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testPacManPolyQuery
specifier|public
name|void
name|testPacManPolyQuery
parameter_list|()
throws|throws
name|Exception
block|{
comment|// pacman
name|double
index|[]
name|px
init|=
block|{
literal|0
block|,
literal|10
block|,
literal|10
block|,
literal|0
block|,
operator|-
literal|8
block|,
operator|-
literal|10
block|,
operator|-
literal|8
block|,
literal|0
block|,
literal|10
block|,
literal|10
block|,
literal|0
block|}
decl_stmt|;
name|double
index|[]
name|py
init|=
block|{
literal|0
block|,
literal|5
block|,
literal|9
block|,
literal|10
block|,
literal|9
block|,
literal|0
block|,
operator|-
literal|9
block|,
operator|-
literal|10
block|,
operator|-
literal|9
block|,
operator|-
literal|5
block|,
literal|0
block|}
decl_stmt|;
comment|// shape bbox
name|double
name|xMinA
init|=
operator|-
literal|10
decl_stmt|;
name|double
name|xMaxA
init|=
literal|10
decl_stmt|;
name|double
name|yMinA
init|=
operator|-
literal|10
decl_stmt|;
name|double
name|yMaxA
init|=
literal|10
decl_stmt|;
comment|// candidate crosses cell
name|double
name|xMin
init|=
literal|2
decl_stmt|;
comment|//-5;
name|double
name|xMax
init|=
literal|11
decl_stmt|;
comment|//0.000001;
name|double
name|yMin
init|=
operator|-
literal|1
decl_stmt|;
comment|//0;
name|double
name|yMax
init|=
literal|1
decl_stmt|;
comment|//5;
comment|// test cell crossing poly
name|assertTrue
argument_list|(
name|GeoRelationUtils
operator|.
name|rectCrossesPolyApprox
argument_list|(
name|yMin
argument_list|,
name|yMax
argument_list|,
name|xMin
argument_list|,
name|yMax
argument_list|,
name|py
argument_list|,
name|px
argument_list|,
name|yMinA
argument_list|,
name|yMaxA
argument_list|,
name|xMinA
argument_list|,
name|xMaxA
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|GeoRelationUtils
operator|.
name|rectCrossesPolyApprox
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|,
operator|-
literal|5
argument_list|,
literal|0.000001
argument_list|,
name|py
argument_list|,
name|px
argument_list|,
name|yMin
argument_list|,
name|yMax
argument_list|,
name|xMin
argument_list|,
name|xMax
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|GeoRelationUtils
operator|.
name|rectWithinPolyApprox
argument_list|(
literal|0
argument_list|,
literal|5
argument_list|,
operator|-
literal|5
argument_list|,
operator|-
literal|2
argument_list|,
name|py
argument_list|,
name|px
argument_list|,
name|yMin
argument_list|,
name|yMax
argument_list|,
name|xMin
argument_list|,
name|xMax
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|testBBoxCrossDateline
specifier|public
name|void
name|testBBoxCrossDateline
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocs
name|td
init|=
name|bboxQuery
argument_list|(
operator|-
literal|45.0
argument_list|,
operator|-
literal|44.0
argument_list|,
literal|179.0
argument_list|,
operator|-
literal|179.0
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"BBoxCrossDateline query failed"
argument_list|,
literal|2
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testWholeMap
specifier|public
name|void
name|testWholeMap
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocs
name|td
init|=
name|bboxQuery
argument_list|(
name|GeoUtils
operator|.
name|MIN_LAT_INCL
argument_list|,
name|GeoUtils
operator|.
name|MAX_LAT_INCL
argument_list|,
name|GeoUtils
operator|.
name|MIN_LON_INCL
argument_list|,
name|GeoUtils
operator|.
name|MAX_LON_INCL
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"testWholeMap failed"
argument_list|,
literal|24
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
name|td
operator|=
name|polygonQuery
argument_list|(
operator|new
name|double
index|[]
block|{
name|GeoUtils
operator|.
name|MIN_LAT_INCL
block|,
name|GeoUtils
operator|.
name|MAX_LAT_INCL
block|,
name|GeoUtils
operator|.
name|MAX_LAT_INCL
block|,
name|GeoUtils
operator|.
name|MIN_LAT_INCL
block|,
name|GeoUtils
operator|.
name|MIN_LAT_INCL
block|}
argument_list|,
operator|new
name|double
index|[]
block|{
name|GeoUtils
operator|.
name|MIN_LON_INCL
block|,
name|GeoUtils
operator|.
name|MIN_LON_INCL
block|,
name|GeoUtils
operator|.
name|MAX_LON_INCL
block|,
name|GeoUtils
operator|.
name|MAX_LON_INCL
block|,
name|GeoUtils
operator|.
name|MIN_LON_INCL
block|}
argument_list|,
literal|20
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"testWholeMap failed"
argument_list|,
literal|24
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
DECL|method|smallTest
specifier|public
name|void
name|smallTest
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocs
name|td
init|=
name|geoDistanceQuery
argument_list|(
literal|40.720611
argument_list|,
operator|-
literal|73.998776
argument_list|,
literal|1
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"smallTest failed"
argument_list|,
literal|2
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
comment|// GeoBoundingBox should not accept invalid lat/lon
DECL|method|testInvalidBBox
specifier|public
name|void
name|testInvalidBBox
parameter_list|()
throws|throws
name|Exception
block|{
name|expectThrows
argument_list|(
name|Exception
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|bboxQuery
argument_list|(
operator|-
literal|92.0
argument_list|,
operator|-
literal|91.0
argument_list|,
literal|179.0
argument_list|,
literal|181.0
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testGeoDistanceQuery
specifier|public
name|void
name|testGeoDistanceQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocs
name|td
init|=
name|geoDistanceQuery
argument_list|(
literal|32.94823588839368
argument_list|,
operator|-
literal|96.4538113027811
argument_list|,
literal|6000
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"GeoDistanceQuery failed"
argument_list|,
literal|2
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
comment|/** see https://issues.apache.org/jira/browse/LUCENE-6905 */
DECL|method|testNonEmptyTermsEnum
specifier|public
name|void
name|testNonEmptyTermsEnum
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocs
name|td
init|=
name|geoDistanceQuery
argument_list|(
operator|-
literal|88.56029371730983
argument_list|,
operator|-
literal|177.23537676036358
argument_list|,
literal|7757.999232959935
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"GeoDistanceQuery failed"
argument_list|,
literal|2
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testMultiValuedQuery
specifier|public
name|void
name|testMultiValuedQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocs
name|td
init|=
name|bboxQuery
argument_list|(
literal|32.7559529921407
argument_list|,
literal|32.7756745755423
argument_list|,
operator|-
literal|96.4538113027811
argument_list|,
operator|-
literal|96.7706036567688
argument_list|,
literal|20
argument_list|)
decl_stmt|;
comment|// 3 single valued docs + 2 multi-valued docs
name|assertEquals
argument_list|(
literal|"testMultiValuedQuery failed"
argument_list|,
literal|5
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testTooBigRadius
specifier|public
name|void
name|testTooBigRadius
parameter_list|()
throws|throws
name|Exception
block|{
name|IllegalArgumentException
name|expected
init|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|geoDistanceQuery
argument_list|(
literal|85.0
argument_list|,
literal|0.0
argument_list|,
literal|4000000
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|expected
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"exceeds maxRadius"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Explicitly large    */
DECL|method|testGeoDistanceQueryHuge
specifier|public
name|void
name|testGeoDistanceQueryHuge
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocs
name|td
init|=
name|geoDistanceQuery
argument_list|(
literal|32.94823588839368
argument_list|,
operator|-
literal|96.4538113027811
argument_list|,
literal|6000000
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"GeoDistanceQuery failed"
argument_list|,
literal|16
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testGeoDistanceQueryCrossDateline
specifier|public
name|void
name|testGeoDistanceQueryCrossDateline
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocs
name|td
init|=
name|geoDistanceQuery
argument_list|(
literal|32.94823588839368
argument_list|,
operator|-
literal|179.9538113027811
argument_list|,
literal|120000
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"GeoDistanceQuery failed"
argument_list|,
literal|3
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
comment|// GeoDistanceQuery should not accept invalid lat/lon as origin
DECL|method|testInvalidGeoDistanceQuery
specifier|public
name|void
name|testInvalidGeoDistanceQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|expectThrows
argument_list|(
name|Exception
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
name|geoDistanceQuery
argument_list|(
literal|92.0
argument_list|,
literal|181.0
argument_list|,
literal|120000
argument_list|,
literal|20
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|testMaxDistanceRangeQuery
specifier|public
name|void
name|testMaxDistanceRangeQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|TopDocs
name|td
init|=
name|geoDistanceRangeQuery
argument_list|(
literal|0.0
argument_list|,
literal|0.0
argument_list|,
literal|10
argument_list|,
literal|20000000
argument_list|,
literal|20
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"GeoDistanceRangeQuery failed"
argument_list|,
literal|24
argument_list|,
name|td
operator|.
name|totalHits
argument_list|)
expr_stmt|;
block|}
DECL|method|testInvalidLatLon
specifier|public
name|void
name|testInvalidLatLon
parameter_list|()
throws|throws
name|Exception
block|{
name|IllegalArgumentException
name|e
decl_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|GeoPointField
argument_list|(
literal|"field"
argument_list|,
literal|180.0
argument_list|,
literal|0.0
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"invalid lat=180.0 for field \"field\""
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|e
operator|=
name|expectThrows
argument_list|(
name|IllegalArgumentException
operator|.
name|class
argument_list|,
parameter_list|()
lambda|->
block|{
operator|new
name|GeoPointField
argument_list|(
literal|"field"
argument_list|,
literal|0.0
argument_list|,
literal|190.0
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"invalid lon=190.0 for field \"field\""
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

