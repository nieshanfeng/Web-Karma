/*******************************************************************************
 * Copyright 2012 University of Southern California
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * This code was developed by the Information Integration Group as part 
 * of the Karma project at the Information Sciences Institute of the 
 * University of Southern California.  For more information, publications, 
 * and related projects, please see: http://www.isi.edu/integration
 ******************************************************************************/

package edu.isi.karma.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DirectedWeightedMultigraph;

import edu.isi.karma.modeling.alignment.GraphPreProcess;
import edu.isi.karma.modeling.alignment.LabeledWeightedEdge;
import edu.isi.karma.modeling.alignment.Name;
import edu.isi.karma.modeling.alignment.NodeType;
import edu.isi.karma.modeling.alignment.SteinerTree;
import edu.isi.karma.modeling.alignment.Vertex;

public class Operation {

	static Logger logger = Logger.getLogger(Operation.class);

	private String baseUri;
	private String id;
	private String name;
	private String description;

	private String method;
	private String address;
	private String addressTemplate;

	private List<Attribute> inputAttributes;
	private List<Attribute> outputAttributes;

	private List<String> variables;

	private Model inputModel;
	private Model outputModel;
	
	private HashMap<String, Attribute> hNodeIdToAttribute;
	
	public Operation(String id) {
		this.id = id;
		hNodeIdToAttribute = new HashMap<String, Attribute>();
		variables = new ArrayList<String>();
		inputAttributes = new ArrayList<Attribute>();
		outputAttributes = new ArrayList<Attribute>();
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getBaseUri() {
		return baseUri;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public String getUri() {
		String uri = "";
		if (getBaseUri() != null) uri += getBaseUri();
		if (getId() != null) uri += getId();
		return uri;
	}

	public Model getInputModel() {
		return inputModel;
	}

	public void setInputModel(Model inputModel) {
		inputModel.setBaseUri(this.getBaseUri());
		this.inputModel = inputModel;
	}

	public Model getOutputModel() {
		return outputModel;
	}

	public void setOutputModel(Model outputModel) {
		outputModel.setBaseUri(this.getBaseUri());
		this.outputModel = outputModel;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public List<Attribute> getInputAttributes() {
		return inputAttributes;
	}

	public void setInputAttributes(List<Attribute> inputAttributes) {
		for (Attribute att : inputAttributes)
			att.setBaseUri(this.getBaseUri());
		this.inputAttributes = inputAttributes;
	}

	public List<Attribute> getOutputAttributes() {
		return outputAttributes;
	}

	public void setOutputAttributes(List<Attribute> outputAttributes) {
		for (Attribute att : outputAttributes)
			att.setBaseUri(this.getBaseUri());
		this.outputAttributes = outputAttributes;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	
	public String getAddressTemplate() {
		if (addressTemplate == null)
			doGrounding();
		
		return addressTemplate;
	}

	
	public void setAddressTemplate(String addressTemplate) {
		this.addressTemplate = addressTemplate;
	}

	public List<String> getVariables() {
		return variables;
	}

	public HashMap<String, Attribute> gethNodeIdToAttribute() {
		return hNodeIdToAttribute;
	}

	public void sethNodeIdToAttribute(HashMap<String, Attribute> hNodeIdToAttribute) {
		this.hNodeIdToAttribute = hNodeIdToAttribute;
	}

	public void updateId(String newId) {
		
		String oldId = this.getId();
		this.setId(newId);
		
		// update the id of input attributes
		for (Attribute att : this.getInputAttributes())
			att.setId(att.getId().replaceFirst(oldId, newId));
		// update the id of output attributes
		for (Attribute att : this.getOutputAttributes())
			att.setId(att.getId().replaceFirst(oldId, newId));
		// update the id of atoms in input model
		if (this.inputModel != null) {
			
			if (this.inputModel.getId() != null)
				this.inputModel.setId(this.inputModel.getId().replaceFirst(oldId, newId));
			
			if (this.inputModel.getAtoms() != null) {
				for (Atom atom : this.inputModel.getAtoms()) {
					if (atom instanceof ClassAtom) {
						ClassAtom classAtom = ((ClassAtom) atom);
						if (classAtom.getArgument1() != null)
							classAtom.getArgument1().setUri(classAtom.getArgument1().getUri().replaceFirst(oldId, newId));
					}
					if (atom instanceof PropertyAtom) {
						PropertyAtom propertyAtom = ((PropertyAtom) atom);
						if (propertyAtom.getArgument1() != null)
							propertyAtom.getArgument1().setUri(propertyAtom.getArgument1().getUri().replaceFirst(oldId, newId));
						if (propertyAtom.getArgument2() != null)
							propertyAtom.getArgument2().setUri(propertyAtom.getArgument2().getUri().replaceFirst(oldId, newId));
					}
				}
			}
		}
		// update the id of atoms in output model
		if (this.outputModel != null) {
			
			if (this.outputModel.getId() != null)
				this.outputModel.setId(this.outputModel.getId().replaceFirst(oldId, newId));
			
			if (this.outputModel.getAtoms() != null) {
				for (Atom atom : this.outputModel.getAtoms()) {
					if (atom instanceof ClassAtom) {
						ClassAtom classAtom = ((ClassAtom) atom);
						if (classAtom.getArgument1() != null)
							classAtom.getArgument1().setUri(classAtom.getArgument1().getUri().replaceFirst(oldId, newId));
					}
					if (atom instanceof PropertyAtom) {
						PropertyAtom propertyAtom = ((PropertyAtom) atom);
						if (propertyAtom.getArgument1() != null)
							propertyAtom.getArgument1().setUri(propertyAtom.getArgument1().getUri().replaceFirst(oldId, newId));
						if (propertyAtom.getArgument2() != null)
							propertyAtom.getArgument2().setUri(propertyAtom.getArgument2().getUri().replaceFirst(oldId, newId));
					}
				}
			}
		}
		
	}
	
	public void updateBaseUri(String newBaseUri) {
		
		this.setBaseUri(newBaseUri);
		
		for (Attribute att : this.getInputAttributes())
			att.setBaseUri(newBaseUri);

		for (Attribute att : this.getOutputAttributes())
			att.setBaseUri(newBaseUri);

		if (this.inputModel != null) 
			this.inputModel.setBaseUri(newBaseUri);

		if (this.outputModel != null) 
			this.outputModel.setBaseUri(newBaseUri);
	}

	private void doGrounding() {
		String str = this.address;
		
		if (this.address == null || this.address.length() == 0) {
			this.addressTemplate = "";
			return;
		}
		
		if (this.inputAttributes == null) {
			this.addressTemplate = this.address;
			return;
		}
		
		// This only works for Web APIs and not RESTful APIs
		for (int i = 0; i < this.inputAttributes.size(); i++) {
			String name = this.inputAttributes.get(i).getName();
			String groundVar = "p" + String.valueOf(i+1);
			int index = str.indexOf(name);
			String temp = str.substring(index);
			if (temp.indexOf("&") != -1)
				temp = temp.substring(0, temp.indexOf("&"));
			if (temp.indexOf("=") != -1)
				temp = temp.substring(temp.indexOf("=") + 1);
			
			str = str.replaceFirst(temp.trim(), "{" + groundVar + "}");
			this.inputAttributes.get(i).setGroundedIn(groundVar);
		}
		
		this.addressTemplate = str;
	}

	public void updateModel(DirectedWeightedMultigraph<Vertex, LabeledWeightedEdge> operationTreeModel) {
		
		if (operationTreeModel == null)
			return;
		
		List<Vertex> inputAttributesNodes = new ArrayList<Vertex>();
		List<Vertex> outputAttributesNodes = new ArrayList<Vertex>();

		this.hNodeIdToAttribute.clear();
		buildHNodeId2AttributeMapping();
		
		// set the rdf ids of all the vertices. The rdf id of leaf vertices are the attribute ids. 
		String hNodeId = "";
		for (Vertex v : operationTreeModel.vertexSet()) {
			if (v.getSemanticType() != null && v.getSemanticType().getHNodeId() != null) {
				logger.debug("Vertex " + v.getLocalID() + " is a semantic type associated to a source columns.");
				hNodeId = v.getSemanticType().getHNodeId();
			} else {
				logger.debug("Vertex " + v.getLocalID() + " is an intermediate node.");
				String variable = this.getId() + "_v" + String.valueOf(variables.size() + 1);
				variables.add(variable);
				v.setRdfId(variable);
				continue;
			}
			
			Attribute att = this.hNodeIdToAttribute.get(hNodeId);
			if (att == null) {
				logger.error("No attribute is associated to the column with semantic type " + v.getID());
				continue;
			}
			
			v.setRdfId(att.getId());
			
			if (att.getIOType() == IOType.INPUT) {
				inputAttributesNodes.add(v);
			}
			if (att.getIOType() == IOType.OUTPUT) {
				outputAttributesNodes.add(v);
			}
		}

		
		List<String> inputModelVertexes = new ArrayList<String>();
		List<String> inputModelEdges = new ArrayList<String>();		
		Model inputModel = getInputModel(operationTreeModel, inputAttributesNodes, inputModelVertexes, inputModelEdges);
		this.setInputModel(inputModel);
		
		Model outputModel = getOutputModel(operationTreeModel, inputModelVertexes, inputModelEdges);
		this.setOutputModel(outputModel);
		
	}
	
	private Model getInputModel(DirectedWeightedMultigraph<Vertex, LabeledWeightedEdge> operationTreeModel, 
			List<Vertex> inputNodes, List<String> inputModelVertexes, List<String> inputModelEdges) {

		if (operationTreeModel == null)
			return null;
				
		logger.debug("compute the steiner tree from the alignment tree with input nodes as steiner nodes ...");
		GraphPreProcess graphPreProcess = new GraphPreProcess(operationTreeModel, inputNodes, null);
		UndirectedGraph<Vertex, LabeledWeightedEdge> undirectedGraph = graphPreProcess.getUndirectedGraph();
		List<Vertex> steinerNodes = graphPreProcess.getSteinerNodes();
		SteinerTree steinerTree = new SteinerTree(undirectedGraph, steinerNodes);


		Model m = new Model(this.getId() + "_inputModel");
		for (Vertex v : steinerTree.getSteinerTree().vertexSet()) {
			
			inputModelVertexes.add(v.getID());
			
			if (v.getNodeType() == NodeType.DataProperty)
				continue;
			
			Name classPredicate = new Name(v.getUri(), v.getNs(), v.getPrefix());
			Name argument1 = new Name(v.getRdfId(), null, null);

			ClassAtom classAtom = new ClassAtom(classPredicate, argument1);
			m.getAtoms().add(classAtom);
		}
		
		for (LabeledWeightedEdge e : steinerTree.getSteinerTree().edgeSet()) {
			
			inputModelEdges.add(e.getID());
			
			Name propertyPredicate = new Name(e.getUri(), e.getNs(), e.getPrefix());
			Name argument1 = new Name(e.getSource().getRdfId(), null, null);
			Name argument2 = new Name(e.getTarget().getRdfId(), null, null);

			PropertyAtom propertyAtom = new PropertyAtom(propertyPredicate, argument1, argument2);
			m.getAtoms().add(propertyAtom);
		}

		return m;
	}

	private Model getOutputModel(DirectedWeightedMultigraph<Vertex, LabeledWeightedEdge> operationTreeModel, 
			List<String> inputModelVertexes, List<String> inputModelEdges) {

		if (operationTreeModel == null)
			return null;

		Model m = new Model(this.getId() + "_outputModel");
		
		for (Vertex v : operationTreeModel.vertexSet()) {
			
			if (inputModelVertexes.indexOf(v.getID()) != -1)
				continue;
			
			if (v.getNodeType() == NodeType.DataProperty)
				continue;
			
			Name classPredicate = new Name(v.getUri(), v.getNs(), v.getPrefix());
			Name argument1 = new Name(v.getRdfId(), null, null);

			ClassAtom classAtom = new ClassAtom(classPredicate, argument1);
			m.getAtoms().add(classAtom);
		}
		
		for (LabeledWeightedEdge e : operationTreeModel.edgeSet()) {
			
			if (inputModelEdges.indexOf(e.getID()) != -1)
				continue;
			
			Name propertyPredicate = new Name(e.getUri(), e.getNs(), e.getPrefix());
			Name argument1 = new Name(e.getSource().getRdfId(), null, null);
			Name argument2 = new Name(e.getTarget().getRdfId(), null, null);

			PropertyAtom propertyAtom = new PropertyAtom(propertyPredicate, argument1, argument2);
			m.getAtoms().add(propertyAtom);
		}
		
		return m;
	}

	public void buildHNodeId2AttributeMapping() {
		for (Attribute att : getInputAttributes()) 
			if (att.gethNodeId() != null)
				this.hNodeIdToAttribute.put(att.gethNodeId(), att);
		for (Attribute att : getOutputAttributes()) 
			if (att.gethNodeId() != null)
				this.hNodeIdToAttribute.put(att.gethNodeId(), att);
	}
	
	public String getInfo() {
		String s = "";
		s += "id=" + this.getId() + ", ";
		s += "name=" + this.getName() + ", ";
		s += "address=" + this.getAddressTemplate() + ", ";
		s += "method=" + this.getMethod();
		return s;
	}
	
	public void print() {
//		System.out.println("id: " + this.getId());
//		System.out.println("name: " + this.getName());
//		System.out.println("address: " + this.getAddressTemplate());
//		System.out.println("method: " + this.getMethod());
//		System.out.println("description: " + this.getDescription());

		System.out.println("********************************************");
		System.out.println("Operation: " + this.getUri());
		System.out.println(getInfo());
		System.out.println("********************************************");
		System.out.println("Input Attributes: ");
		for (Attribute p : getInputAttributes())
			p.print();
		System.out.println("********************************************");
		System.out.print("Input Model: ");
		if (this.inputModel != null) {
			System.out.println(inputModel.getUri());
			this.inputModel.print();
		}
		System.out.println("********************************************");
		System.out.println("Output Attributes: ");
		for (Attribute p : getOutputAttributes())
			p.print();
		System.out.println("********************************************");
		System.out.print("Output Model: ");
		if (this.outputModel != null) {
			System.out.println(outputModel.getUri());
			this.outputModel.print();
		}
	}
	
}