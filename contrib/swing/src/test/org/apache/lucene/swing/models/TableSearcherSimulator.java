begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.swing.models
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|swing
operator|.
name|models
package|;
end_package

begin_comment
comment|/**  * Copyright 2005 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|ActionListener
import|;
end_import

begin_import
import|import
name|java
operator|.
name|awt
operator|.
name|event
operator|.
name|ActionEvent
import|;
end_import

begin_comment
comment|/**  * @author Jonathan Simon - jonathan_s_simon@yahoo.com  */
end_comment

begin_class
DECL|class|TableSearcherSimulator
specifier|public
class|class
name|TableSearcherSimulator
block|{
DECL|method|TableSearcherSimulator
specifier|public
name|TableSearcherSimulator
parameter_list|()
block|{
name|JFrame
name|frame
init|=
operator|new
name|JFrame
argument_list|()
decl_stmt|;
name|frame
operator|.
name|setBounds
argument_list|(
literal|200
argument_list|,
literal|200
argument_list|,
literal|400
argument_list|,
literal|250
argument_list|)
expr_stmt|;
name|JTable
name|table
init|=
operator|new
name|JTable
argument_list|()
decl_stmt|;
specifier|final
name|BaseTableModel
name|tableModel
init|=
operator|new
name|BaseTableModel
argument_list|(
name|DataStore
operator|.
name|getRestaurants
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|TableSearcher
name|searchTableModel
init|=
operator|new
name|TableSearcher
argument_list|(
name|tableModel
argument_list|)
decl_stmt|;
name|table
operator|.
name|setModel
argument_list|(
name|searchTableModel
argument_list|)
expr_stmt|;
name|JScrollPane
name|scrollPane
init|=
operator|new
name|JScrollPane
argument_list|(
name|table
argument_list|)
decl_stmt|;
specifier|final
name|JTextField
name|searchField
init|=
operator|new
name|JTextField
argument_list|()
decl_stmt|;
name|JButton
name|searchButton
init|=
operator|new
name|JButton
argument_list|(
literal|"Go"
argument_list|)
decl_stmt|;
name|ActionListener
name|searchListener
init|=
operator|new
name|ActionListener
argument_list|()
block|{
specifier|public
name|void
name|actionPerformed
parameter_list|(
name|ActionEvent
name|e
parameter_list|)
block|{
name|searchTableModel
operator|.
name|search
argument_list|(
name|searchField
operator|.
name|getText
argument_list|()
operator|.
name|trim
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|searchField
operator|.
name|requestFocus
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|searchButton
operator|.
name|addActionListener
argument_list|(
name|searchListener
argument_list|)
expr_stmt|;
name|searchField
operator|.
name|addActionListener
argument_list|(
name|searchListener
argument_list|)
expr_stmt|;
name|frame
operator|.
name|getContentPane
argument_list|()
operator|.
name|setLayout
argument_list|(
operator|new
name|BorderLayout
argument_list|()
argument_list|)
expr_stmt|;
name|frame
operator|.
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|scrollPane
argument_list|,
name|BorderLayout
operator|.
name|CENTER
argument_list|)
expr_stmt|;
name|JPanel
name|searchPanel
init|=
operator|new
name|JPanel
argument_list|()
decl_stmt|;
name|searchPanel
operator|.
name|setLayout
argument_list|(
operator|new
name|BorderLayout
argument_list|(
literal|10
argument_list|,
literal|10
argument_list|)
argument_list|)
expr_stmt|;
name|searchPanel
operator|.
name|add
argument_list|(
name|searchField
argument_list|,
name|BorderLayout
operator|.
name|CENTER
argument_list|)
expr_stmt|;
name|searchPanel
operator|.
name|add
argument_list|(
name|searchButton
argument_list|,
name|BorderLayout
operator|.
name|EAST
argument_list|)
expr_stmt|;
name|JPanel
name|topPanel
init|=
operator|new
name|JPanel
argument_list|(
operator|new
name|BorderLayout
argument_list|()
argument_list|)
decl_stmt|;
name|topPanel
operator|.
name|add
argument_list|(
name|searchPanel
argument_list|,
name|BorderLayout
operator|.
name|CENTER
argument_list|)
expr_stmt|;
name|topPanel
operator|.
name|add
argument_list|(
operator|new
name|JPanel
argument_list|()
argument_list|,
name|BorderLayout
operator|.
name|EAST
argument_list|)
expr_stmt|;
name|topPanel
operator|.
name|add
argument_list|(
operator|new
name|JPanel
argument_list|()
argument_list|,
name|BorderLayout
operator|.
name|WEST
argument_list|)
expr_stmt|;
name|topPanel
operator|.
name|add
argument_list|(
operator|new
name|JPanel
argument_list|()
argument_list|,
name|BorderLayout
operator|.
name|NORTH
argument_list|)
expr_stmt|;
name|topPanel
operator|.
name|add
argument_list|(
operator|new
name|JPanel
argument_list|()
argument_list|,
name|BorderLayout
operator|.
name|SOUTH
argument_list|)
expr_stmt|;
name|frame
operator|.
name|getContentPane
argument_list|()
operator|.
name|add
argument_list|(
name|topPanel
argument_list|,
name|BorderLayout
operator|.
name|NORTH
argument_list|)
expr_stmt|;
name|frame
operator|.
name|setTitle
argument_list|(
literal|"Lucene powered table searching"
argument_list|)
expr_stmt|;
name|frame
operator|.
name|setDefaultCloseOperation
argument_list|(
name|JFrame
operator|.
name|EXIT_ON_CLOSE
argument_list|)
expr_stmt|;
name|frame
operator|.
name|show
argument_list|()
expr_stmt|;
block|}
DECL|method|main
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
operator|new
name|TableSearcherSimulator
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

