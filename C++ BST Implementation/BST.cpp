#include "BST.h"
#include <vector>
#include <iostream>
#include <iomanip>

void recursiveDelete(BST::Node* n){
    if(n){
        recursiveDelete(n->leftChild);
        recursiveDelete(n->rightChild);
        delete n;
    }
}


/**
 * Implement the BST constructor
 */
BST::BST() {
    this->root = nullptr;
    this->numElements = 0;
}

/**
 * Implement the BST destructor
 */
BST::~BST() {
    clear();
}

/**
 * Implement size() correctly
 */
unsigned int BST::size() const {
    return this->numElements;
}




/**
 * Implement clear() correctly without memory leaks
 */
void BST::clear() {
    recursiveDelete(root);
    root = nullptr;
    numElements = 0;
    /*
    Node* currNode = root;
    Node* temp;
    while(numElements > 0){
        cout << currNode->leftChild;
        while(currNode->leftChild != nullptr){
    
            currNode = currNode->leftChild;
        }

        cout << currNode->rightChild;
        while(currNode->rightChild != nullptr){
            cout << "2\n";
            currNode = currNode->rightChild;
        }
        if(currNode->parent != nullptr){
            temp = currNode->parent;
        }
        delete currNode;
        numElements--;
        currNode = temp;
    }
    this->root = nullptr; */
}

/**
 * Implement insert() correctly
 */
bool BST::insert(int element) {
    if(find(element)){
        return false;
    }

    if(this->numElements == 0){
        this->root = new Node(element);
        numElements++;
        return true;        
    }

    Node* currNode = this->root;
    Node* currParent = nullptr;

    //find correct node location
    while(currNode != nullptr){
        if(currNode->data < element){
            currParent = currNode;
            currNode = currNode->rightChild;
        }   
        else{
            currParent = currNode;
            currNode = currNode->leftChild;
        }
    }
    if(currParent){

    //after finding bottom node, put in right place
    if(currParent->data > element){
        Node* temp = new Node(element);
        currParent->leftChild = temp;
        temp->parent = currParent;
    }
    else{
        Node* temp = new Node(element);
        currParent->rightChild = temp;
        temp->parent = currParent;
    }
     numElements++;
     return true; 
    } 
    return false;  
}

/**
 * Implement find() correctly
 */
bool BST::find(const int & query) const {
    if(numElements == 0){
        return false;
    }
    else{
        Node* currNode = this->root;
        while (currNode != nullptr ){
            if(query == currNode->data){
                return true;
            }

            else if(query < currNode->data){
                currNode = currNode->leftChild;
            }

            else if(query > currNode->data){
                currNode = currNode->rightChild;
            }
        }
        return false;
    }
}

/**
 * Implement the getLeftMostNode() function correctly
 */
BST::Node* BST::getLeftMostNode() {
    Node* itr = this->root;
    if(itr != nullptr){
        while (itr->leftChild != nullptr){
            itr = itr->leftChild;
        }
    }
    return itr;
}

/**
 * Implement the BST::Node successor function correctly
 */
BST::Node* BST::Node::successor() {
   if(this->rightChild != nullptr){
        Node* itr = this->rightChild;
        while (itr->leftChild != nullptr){
            itr = itr->leftChild;
        }
        return itr;
   } 
   else{
       Node* currNode = this;
       Node* currParent = currNode->parent;
       while(currNode->parent != nullptr){
           currParent = currNode->parent;
           if(currNode == currParent->leftChild){
               return currParent;
           }
           currNode = currParent;
       }
       return nullptr;
   }
}
/*
int main(){
    BST* a = new BST();
    a->insert(5);
    a->insert(3);
    a->insert(2);
    a->insert(1);
    cout << boolalpha <<  a->find(5);
    cout << boolalpha <<  a->find(3);
    cout << boolalpha <<  a->find(2);
    cout << boolalpha <<  a->find(1);
    cout << a->size();
    return 0;
}*/
