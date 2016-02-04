package com.signity.bonbon.cart;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.signity.bonbon.Utilities.PrefManager;
import com.signity.bonbon.model.CartProductModel;
import com.signity.bonbon.model.Product;
import com.signity.bonbon.model.ProductListModel;
import com.signity.bonbon.model.SelectedVariant;
import com.signity.bonbon.model.UpdateCartModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by rajinder on 8/10/15.
 */
public class CartHelper {

    public static String TAG = CartHelper.class.getSimpleName();

    public static HashMap<String, Product> getUserProductCart(Context context) {
        PrefManager prefManager = new PrefManager(context);
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Product>>() {
        }.getType();
        String json = prefManager.getSharedValue(PrefManager.PREF_CART_OBJECT);
        HashMap<String, Product> listCartOjects = gson.fromJson(json, type);
        if (listCartOjects == null) {
            listCartOjects = new HashMap<>();
        }
        return listCartOjects;
    }

    public static void setUserProductCart(Context context, HashMap<String, CartProductModel> listCartObject) {
        PrefManager prefManager = new PrefManager(context);
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Product>>() {
        }.getType();
        String json = gson.toJson(listCartObject, type);
        prefManager.storeSharedValue(PrefManager.PREF_CART_OBJECT, json);
    }


    public static void addProductToCart(Context context, String productVarientId, Product product) {
        HashMap<String, Product> listCartObject = getUserProductCart(context);
        listCartObject.put(productVarientId, product);
        PrefManager prefManager = new PrefManager(context);
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Product>>() {
        }.getType();
        String json = gson.toJson(listCartObject, type);
        prefManager.storeSharedValue(PrefManager.PREF_CART_OBJECT, json);
    }

    public static void removeProductToCart(Context context, String productVarientId) {
        HashMap<String, Product> listCartObject = getUserProductCart(context);
        listCartObject.remove(productVarientId);
        PrefManager prefManager = new PrefManager(context);
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Product>>() {
        }.getType();
        String json = gson.toJson(listCartObject, type);
        prefManager.storeSharedValue(PrefManager.PREF_CART_OBJECT, json);
    }

    public static void updateCartFromCheckout(Context context, String variantId, SelectedVariant selectedVariant) {
        HashMap<String, Product> listCartObject = getUserProductCart(context);
        Product product = listCartObject
                .get(variantId);
        product.setSelectedVariant(selectedVariant);
        listCartObject.put(variantId, product);
        PrefManager prefManager = new PrefManager(context);
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Product>>() {
        }.getType();
        String json = gson.toJson(listCartObject, type);
        prefManager.storeSharedValue(PrefManager.PREF_CART_OBJECT, json);
    }

    public static void removeCartFromCheckout(Context context, String variantId) {
        HashMap<String, Product> listCartObject = getUserProductCart(context);
        listCartObject.remove(variantId);
        PrefManager prefManager = new PrefManager(context);
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, Product>>() {
        }.getType();
        String json = gson.toJson(listCartObject, type);
        prefManager.storeSharedValue(PrefManager.PREF_CART_OBJECT, json);
    }

    public static Product getProductFromCart(Context context, String productVarientId) {
        HashMap<String, Product> listCartObject = getUserProductCart(context);
        Product product = listCartObject.get(productVarientId);
        return product;
    }

    public static int getCartSize(Context context) {
        int size = 0;
        return getItemForCount(context).size();
    }

    public static HashMap<String, String> getItemForCount(Context context) {
        HashMap<String, String> listCartOjects = null;
        PrefManager prefManager = new PrefManager(context);
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, String>>() {
        }.getType();
        String json = prefManager.getSharedValue(PrefManager.PREF_CART_SIZE);
        if (!json.isEmpty()) {
            listCartOjects = gson.fromJson(json, type);
            if (listCartOjects == null) {
                listCartOjects = new HashMap<>();
            }
        } else {
            listCartOjects = new HashMap<>();
        }

        return listCartOjects;
    }

    public static String getCartTotalPrice(Context context) {
        double totalPrice = 0.0;
        HashMap<String, UpdateCartModel> listCartObjectMap = getUserOrderCart(context);
        Iterator it = listCartObjectMap.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + " = " + (UpdateCartModel) pair.getValue());
            UpdateCartModel updateCartModel = (UpdateCartModel) pair.getValue();
            int quan = Integer.parseInt(updateCartModel.getQuantity());
            double price = Double.parseDouble(updateCartModel.getPrice());
            double productPrice = quan * price;
            totalPrice = totalPrice + productPrice;
            it.remove(); // avoids a ConcurrentModificationException
        }

        return String.valueOf(totalPrice);
    }

//    public static String getCartJsonOject(Context context) {
//        String jsonString = "";
//        HashMap<String, Product> listCartObjectMap = getUserProductCart(context);
//        Iterator it = listCartObjectMap.entrySet().iterator();
//        List<UpdateCartModel> updateCartModelList = new ArrayList<UpdateCartModel>();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry) it.next();
//            Product product = (Product) pair.getValue();
//            SelectedVariant selectedVariant = product.getSelectedVariant();
//            Variant variant = product.getVariants().get(selectedVariant.getVarientPosition());
//            UpdateCartModel updateCartModel = new UpdateCartModel();
//            updateCartModel.setProductId(product.getId());
//            updateCartModel.setVariantId(selectedVariant.getVariantId());
//            updateCartModel.setWeight(selectedVariant.getWeight());
//            updateCartModel.setMrpPrice(selectedVariant.getMrpPrice());
//            updateCartModel.setPrice(selectedVariant.getPrice());
//            updateCartModel.setDiscount(selectedVariant.getDiscount());
//            updateCartModel.setUnitType(selectedVariant.getUnitType());
//            updateCartModel.setQuantity(selectedVariant.getQuantity());
//            updateCartModelList.add(updateCartModel);
//            it.remove(); // avoids a ConcurrentModificationException
//        }
//
//        Gson gson = new Gson();
//        Type type = new TypeToken<List<UpdateCartModel>>() {
//        }.getType();
//        jsonString = gson.toJson(updateCartModelList, type);
//        Log.e("TAG", jsonString);
//        return jsonString;
//    }


    public static String getCartSingleItem(Context context, String productId, SelectedVariant selectedVariant) {
        String jsonString = "";
        List<UpdateCartModel> updateCartModelList = new ArrayList<UpdateCartModel>();
        UpdateCartModel updateCartModel = new UpdateCartModel();
        updateCartModel.setProductId(productId);
        updateCartModel.setVariantId(selectedVariant.getVariantId());
        updateCartModel.setWeight(selectedVariant.getWeight());
        updateCartModel.setMrpPrice(selectedVariant.getMrpPrice());
        updateCartModel.setPrice(selectedVariant.getPrice());
        updateCartModel.setDiscount(selectedVariant.getDiscount());
        updateCartModel.setUnitType(selectedVariant.getUnitType());
        updateCartModel.setQuantity(selectedVariant.getQuantity());
        updateCartModelList.add(updateCartModel);
        Gson gson = new Gson();
        Type type = new TypeToken<List<UpdateCartModel>>() {
        }.getType();
        jsonString = gson.toJson(updateCartModelList, type);
        Log.e("TAG", jsonString);
        return jsonString;
    }

    // All helper method for history of cart  below here

    /**
     * @param context
     * @return
     */
    public static HashMap<String, UpdateCartModel> getUserOrderCart(Context context) {

        PrefManager prefManager = new PrefManager(context);
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, UpdateCartModel>>() {
        }.getType();
        String json = prefManager.getSharedValue(PrefManager.PREF_ORDER_OBJECT);
        HashMap<String, UpdateCartModel
                > listCartOjects = gson.fromJson(json, type);
        if (listCartOjects == null) {
            listCartOjects = new HashMap<>();
        }
        return listCartOjects;
    }

    public static void updateCartHistoryOrderPref(Context context, ProductListModel productListModel) {
        HashMap<String, UpdateCartModel> cartOrder = new HashMap<>();
        HashMap<String, String> mapCartItemCount = new HashMap<String, String>();

        for (Product product : productListModel.getData()) {
            SelectedVariant selectedVariant = product.getSelectedVariant();
            UpdateCartModel updateCartModel = new UpdateCartModel();
            updateCartModel.setProductId(product.getId());
            updateCartModel.setVariantId(selectedVariant.getVariantId());
            updateCartModel.setWeight(selectedVariant.getWeight());
            updateCartModel.setMrpPrice(selectedVariant.getMrpPrice());
            updateCartModel.setPrice(selectedVariant.getPrice());
            updateCartModel.setDiscount(selectedVariant.getDiscount());
            updateCartModel.setUnitType(selectedVariant.getUnitType());
            updateCartModel.setQuantity(selectedVariant.getQuantity());
            cartOrder.put(selectedVariant.getVariantId(), updateCartModel);
            mapCartItemCount.put(selectedVariant.getVariantId(), updateCartModel.getQuantity());
        }
        PrefManager prefManager = new PrefManager(context);
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, UpdateCartModel>>() {
        }.getType();
        String json = gson.toJson(cartOrder, type);
        prefManager.storeSharedValue(PrefManager.PREF_ORDER_OBJECT, json);
        Type type2 = new TypeToken<HashMap<String, String>>() {
        }.getType();
        String json2 = gson.toJson(mapCartItemCount, type2);
        prefManager.storeSharedValue(PrefManager.PREF_CART_SIZE, json2);
    }

    public static void addItemToCartHistoryObj(Context context, String productVarientId, Product product) {
        HashMap<String, UpdateCartModel> cartOrder = getUserOrderCart(context);
        SelectedVariant selectedVariant = product.getSelectedVariant();
        UpdateCartModel updateCartModel = new UpdateCartModel();
        updateCartModel.setProductId(product.getId());
        updateCartModel.setVariantId(selectedVariant.getVariantId());
        updateCartModel.setWeight(selectedVariant.getWeight());
        updateCartModel.setMrpPrice(selectedVariant.getMrpPrice());
        updateCartModel.setPrice(selectedVariant.getPrice());
        updateCartModel.setDiscount(selectedVariant.getDiscount());
        updateCartModel.setUnitType(selectedVariant.getUnitType());
        updateCartModel.setQuantity(selectedVariant.getQuantity());
        cartOrder.put(productVarientId, updateCartModel);
        PrefManager prefManager = new PrefManager(context);
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, UpdateCartModel>>() {
        }.getType();
        String json = gson.toJson(cartOrder, type);
        prefManager.storeSharedValue(PrefManager.PREF_ORDER_OBJECT, json);

        HashMap<String, String> mapCartItemCount = getItemForCount(context);
        mapCartItemCount.put(selectedVariant.getVariantId(), selectedVariant.getQuantity());
        Type type2 = new TypeToken<HashMap<String, String>>() {
        }.getType();
        String json2 = gson.toJson(mapCartItemCount, type2);
        prefManager.storeSharedValue(PrefManager.PREF_CART_SIZE, json2);
    }

    public static void removeCartOrderObj(Context context, String productVarientId, Product product) {
        HashMap<String, UpdateCartModel> cartOrder = getUserOrderCart(context);
        SelectedVariant selectedVariant = product.getSelectedVariant();
        UpdateCartModel updateCartModel = new UpdateCartModel();
        updateCartModel.setProductId(product.getId());
        updateCartModel.setVariantId(selectedVariant.getVariantId());
        updateCartModel.setWeight("0");
        updateCartModel.setMrpPrice("0");
        updateCartModel.setPrice("0");
        updateCartModel.setDiscount("0");
        updateCartModel.setUnitType("0");
        updateCartModel.setQuantity("0");
        cartOrder.put(productVarientId, updateCartModel);
        PrefManager prefManager = new PrefManager(context);
        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, UpdateCartModel>>() {
        }.getType();
        String json = gson.toJson(cartOrder, type);
        prefManager.storeSharedValue(PrefManager.PREF_ORDER_OBJECT, json);
        HashMap<String, String> mapCartItemCount = getItemForCount(context);
        mapCartItemCount.remove(selectedVariant.getVariantId());
        Type type2 = new TypeToken<HashMap<String, String>>() {
        }.getType();
        String json2 = gson.toJson(mapCartItemCount, type2);
        prefManager.storeSharedValue(PrefManager.PREF_CART_SIZE, json2);
    }


    public static String getCartItemQuantity(Context context, String productVariantId) {
        String quantity = "0";
        HashMap<String, UpdateCartModel> cartOrder = getUserOrderCart(context);

        if (cartOrder.containsKey(productVariantId)) {
            UpdateCartModel updateCartModel = cartOrder.get(productVariantId);
            quantity = updateCartModel.getQuantity();
        }
        return quantity;
    }

    public static String getOrderStringJson(Context context) {
        String jsonString = "";
        HashMap<String, UpdateCartModel> listCartObjectMap = getUserOrderCart(context);
        Iterator it = listCartObjectMap.entrySet().iterator();
        List<UpdateCartModel> updateCartModelList = new ArrayList<UpdateCartModel>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            UpdateCartModel updateCartModel = (UpdateCartModel) pair.getValue();
            updateCartModelList.add(updateCartModel);
            it.remove(); // avoids a ConcurrentModificationException
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<UpdateCartModel>>() {
        }.getType();
        jsonString = gson.toJson(updateCartModelList, type);
        Log.e("TAG", jsonString);
        return jsonString;
    }

    public static String getOrderStringForSubmit(Context context) {
        String jsonString = "";
        JSONArray order = new JSONArray();

        HashMap<String, UpdateCartModel> listCartObjectMap = getUserOrderCart(context);
        Iterator it = listCartObjectMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            UpdateCartModel updateCartModel = (UpdateCartModel) pair.getValue();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("product_id", updateCartModel.getProductId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                jsonObject.put("variant_id", updateCartModel.getVariantId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            order.put(jsonObject);
            it.remove(); // avoids a ConcurrentModificationException
        }
        Log.e("TAG", order.toString());
        return order.toString();
    }


}
