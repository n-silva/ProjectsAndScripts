import gql from "graphql-tag";
import { useQuery } from "@apollo/client";

const GET_IDS = gql`
  {
    boxShapes {
        id
    }
  }
`;

const BOX_QUERY = gql`
  {
    boxShapes {
        color
        id
        posX
        posY
        sizeX
        sizeY
    }
  }
`;

const CREATE_BOX = gql`
  mutation AddBox($id: String!, $posX: Float!, $posY: Float!, $sizeX: Float!, $sizeY: Float!, $color: String!) {
    addBoxShape(
        boxShapeData: {id: $id, color: $color, posX: $posX, posY: $posY, sizeX: $sizeX, sizeY: $sizeY}
        ) {
        id
        color
        posX
        posY
        sizeX
        sizeY
    }
  }
`;

const DELETE_BOX = gql`
  mutation deleteBox($id: String!) {
    delBoxShape(boxShapeId:  $id)
  }
`;

const DELETE_ALL_BOX = gql`
  mutation deleteAllBox {
    delAllBoxShape
  }
`;

const BOX_COUNTER_SUBSCRIPTION = gql`
    subscription boxCounter {
        count
    }
`;

function LoadData(){
  const { data, loading, error } = useQuery(BOX_QUERY);
  var nodes = [];
  
  if (loading) return false;
  if (error) return <pre>{error.message}</pre>

  for (const box of data?.boxShapes) {
      nodes.push({
          id: box.id,
          color: box.color,
          width: box.sizeX,
          height: box.sizeY,
          x: box.posX,
          y: box.posY
      });
  }
  
  return nodes;
}


export {
  LoadData, 
  CREATE_BOX, 
  BOX_QUERY, 
  DELETE_BOX, 
  DELETE_ALL_BOX, 
  BOX_COUNTER_SUBSCRIPTION, 
  GET_IDS
};
